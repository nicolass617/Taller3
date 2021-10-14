package com.edu.unbosque.webService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.edu.unbosque.connectiobd.ConnectionBD;
import com.edu.unbosque.model.FeaturesVO;
import com.edu.unbosque.model.GeoJsonVO;
import com.edu.unbosque.model.GeometryVO;
import com.edu.unbosque.model.MascotaVO;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase que hace nuestro webService con MongoDB y Jedis
 */
@Path("/petsvc")
public class MascotaSvc extends ConnectionBD{
	//Referencia de la colección Mascota en nuestra base de datos 
	private static final DBCollection collection = database.getCollection("Mascota");
	private static Gson gson = new Gson();
	
	
	/**
	 * Método que guarda una mascota en nuestra bases de datos de MongoDB y además,
	 * extrae su ubicación y timestamp para persistirlo en Redis
	 * @param m Un objeto mascota que será guardado 
	 * @return Un string cuando se logra guardar exitosamente nuestro registro
	 */
	@POST
	@Path("/addpet")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String addPet(MascotaVO m) {
		try {
			//Se instancia documento
			BasicDBObject doc = new BasicDBObject();
			//Al objeto se le setea el tiempo actual
			m.setTimestamp(new Date());
			//Se tiene el objeto y se transforma a Json
			String obj = gson.toJson(m);
			//Se parsea el Json en un documento
			doc = BasicDBObject.parse(obj);
			//Se inserta ese documento en la colección
			collection.insert(doc);
			//Se extraen los datos de localicación y timestamp y se concatenan en un solo string
			String value = m.getGeolocation().getLatitude() + "," + m.getGeolocation().getLongitude() + "," + m.getTimestamp();
			//Se persiste la clave, que será el microchip y el valor, que será nuestro string que acabamos de crear
			jedis.lpush(m.getMicrochip(), value);
			//Le ponemos expiración de 3600 segundos, es decir, una hora, como lo dice nuestro ejercicio
			jedis.expire(m.getMicrochip(), 3600);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "Se ha logrado guardar el registro";
	}
	
	/**
	 * Método que lee en redis las coordenadas que tiene una mascota en su última hora, después con esas coordenadas
	 * se construye un GEOJSon, y se retorna
	 * @param m Una mascota a la cuál veremos su localización en la la última hora
	 * @return un GeoJSon del recorrido de la mascota
	 */
	@GET
	@Path("/getlocation")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public GeoJsonVO getLocations(MascotaVO m) {
		//Se instancia un GeoJson
		GeoJsonVO geo = new GeoJsonVO();
		try {
			//Se mira qué tan grande es una lista
			long l = jedis.llen(m.getMicrochip());
			//Se hace un lRange, desde 0 hasta l, para obtener la lista de las coordenadas y se asignan a una lista
			List<String> locations = jedis.lrange(m.getMicrochip(), 0, l);
			List<List<Double>> coordinates = new ArrayList<List<Double>>();
			List<FeaturesVO> features = new ArrayList<FeaturesVO>();
			
			//Se ponen los types 
			geo.setType(jedis.get("type1"));

			//Se ponen los types 
			FeaturesVO f = new FeaturesVO();
			f.setType(jedis.get("type2"));

			//Se ponen los types 
			GeometryVO gm = new GeometryVO();
			gm.setType(jedis.get("type3"));
			
			//Ciclo que recorre las ubicaciones, para construir la geometry
			for (int i = 0; i < locations.size(); i++) {
				//String con la posición i de nuestra lista de locations
				String coord = locations.get(i);
				
				//Un split para separar las ubicaciones con ","
				String[] strs = coord.split(",");
				
				List<Double> db = new ArrayList<Double>();
				
				//Se construye la ubicación con dos cadenas y se parsean a Double
				db.add(Double.parseDouble(strs[0]));
				db.add(Double.parseDouble(strs[1]));
				
				//Se añade esa lista, a una lista de listas de Doubles 
				coordinates.add(db);
				
				//Se añaden esas coordinadas a Geometry
				gm.setCoordinates(coordinates);
			}
			
			//Esa geometry, se añade a un objeo feature
			f.setGeometry(gm);
			
			//Esa feature, se añade a una lista de features
			features.add(f);
			
			//Se añade a lista a un GeoJSon
			geo.setFeatures(features);
		} catch (Exception e) {
			
		}
		
		//Se retorna el geoJSon creado
		return geo;
	}
	
	@GET
	@Path("/findRareSings")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public List<DBObject> findRareSings(MascotaVO m, @QueryParam("DateInit") Date init, @QueryParam("DateEnd") Date fin) {
		
		BasicDBObject inQuery = new BasicDBObject();

		List<Date> list = new ArrayList<Date>();
		list.add(init);
		list.add(fin);
		inQuery.put("timestamp", new BasicDBObject("$in", list));

		BasicDBObject andQuery1 = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("microchip", m.getMicrochip()));
		obj.add(inQuery);
		andQuery1.put("$and", obj);

		String specie = collection.findOne(new BasicDBObject("microchip", m.getMicrochip())).get("species").toString();
		BasicDBObject andQuery2 = new BasicDBObject();
		BasicDBObject orQuery = new BasicDBObject();
		if (specie.equalsIgnoreCase("Cat")) {
			Double temp = 39.2;
			Double heart_rate = 200.0;
			Double breathing_frecuency = 30.0;

			List<BasicDBObject> orQ = new ArrayList<BasicDBObject>();
			orQ.add(new BasicDBObject(new BasicDBObject("vital_signs.temperature", new BasicDBObject("$gte", temp))));
			orQ.add(new BasicDBObject("vital_signs.breathing_frecuency",
					new BasicDBObject("$gte", breathing_frecuency)));
			orQ.add(new BasicDBObject("vital_signs.heart_rate", new BasicDBObject("$gte", heart_rate)));
			orQuery.put("$or", orQ);

			List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
			obj1.add(orQuery);
			obj1.add(andQuery1);
			andQuery2.put("$and", obj1);
		} else if (specie.equalsIgnoreCase("Dog")) {
			Double temp = 39.2;
			Double heart_rate = 120.0;
			Double breathing_frecuency = 30.0;

			List<BasicDBObject> orQ = new ArrayList<BasicDBObject>();
			orQ.add(new BasicDBObject(new BasicDBObject("vital_signs.temperature", new BasicDBObject("$gte", temp))));
			orQ.add(new BasicDBObject("vital_signs.breathing_frecuency",
					new BasicDBObject("$gte", breathing_frecuency)));
			orQ.add(new BasicDBObject("vital_signs.heart_rate", new BasicDBObject("$gte", heart_rate)));
			orQuery.put("$or", orQ);

			List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
			obj1.add(orQuery);
			obj1.add(andQuery1);
			andQuery2.put("$and", obj1);
		}
		
		DBCursor dbc = collection.find(andQuery2);
		List<DBObject> objs = new ArrayList<DBObject>();
		
		for(Iterator<DBObject> it = dbc.iterator(); dbc.hasNext();) {
			objs.add(it.next());
		}
		
		return objs;
	}

}
