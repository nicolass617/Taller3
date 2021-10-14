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
	
	
	/**
	 * 
	 * @param m una mascota a la cuál veremos si tiene signos vitales fuera de lo normal en un rango de fechas
	 * @param init fecha desde donde veremos los registros de una mascota
	 * @param fin fecha hasta donde veremos los registros de una mascota
	 * @return documentos que cumplen con las condiciones de nuestro query
	 */
	@GET
	@Path("/findRareSings")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public List<DBObject> findRareSings(MascotaVO m, @QueryParam("DateInit") Date init, @QueryParam("DateEnd") Date fin) {
		
		
		
		//Query de in, donde se buscarán los documentos que estén entre un rango de dos fechas
		BasicDBObject inQuery = new BasicDBObject();

		//Se crea una lista donde estarán las dos dates para nuestro in
		List<DBObject> list = new ArrayList<DBObject>();
		
		//Se añaden los dates a la lista
		list.add( new BasicDBObject( "timestamp",new BasicDBObject("$gte",init)));
		list.add(new BasicDBObject( "timestamp", new BasicDBObject("$lte",fin)));
		
		//Query de in
		inQuery.put("$and", list);

		//Se une el query anterior mediante un AND, con una consulta de que el microchip sea igual al microchip del JSon que entra al método
		BasicDBObject andQuery1 = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("microchip", m.getMicrochip()));
		obj.add(inQuery);
		andQuery1.put("$and", obj);

		//Se obtiene la especie de la mascota que está entrando en el json, para posteriormente entrar a un IF ELSE, dependiendo de si es Cat o Dog
		String specie = collection.findOne(new BasicDBObject("microchip", m.getMicrochip())).get("species").toString();
		BasicDBObject andQuery2 = new BasicDBObject();
		BasicDBObject orQuery = new BasicDBObject();
		BasicDBObject orQuery2 = new BasicDBObject();
		if (specie.equalsIgnoreCase("Cat")) {
			
			//Signos vitales para un gato desde donde se considera anormal
			Double temp = 39.2;
			Double heart_rate = 200.0;
			Double breathing_frecuency = 30.0;

			//Or multiple, donde se compara si cada signo vital está por encima del valor sugerido 
			List<BasicDBObject> orQ = new ArrayList<BasicDBObject>();
			orQ.add(new BasicDBObject(new BasicDBObject("vital_signs.temperature", new BasicDBObject("$gte", temp))));
			orQ.add(new BasicDBObject("vital_signs.breathing_frecuency",
					new BasicDBObject("$gte", breathing_frecuency)));
			orQuery.put("$or", orQ);
			
			List<BasicDBObject> orQ2 = new ArrayList<BasicDBObject>();
			orQ2.add(new BasicDBObject("vital_signs.heart_rate", new BasicDBObject("$gte", heart_rate)));
			orQ2.add(orQuery);
			orQuery2.put("$or", orQ2);
			
			
			//Se une la consulta que llevábamos, con el or multiple 
			List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
			obj1.add(orQuery);
			obj1.add(andQuery1);
			andQuery2.put("$and", obj1);
		} else if (specie.equalsIgnoreCase("Dog")) {
			
			//Signos vitales para un perro desde donde se considera anormal
			Double temp = 39.2;
			Double heart_rate = 120.0;
			Double breathing_frecuency = 30.0;

			//Or multiple, donde se compara si cada signo vital está por encima del valor sugerido 
			List<BasicDBObject> orQ = new ArrayList<BasicDBObject>();
			orQ.add(new BasicDBObject(new BasicDBObject("vital_signs.temperature", new BasicDBObject("$gte", temp))));
			orQ.add(new BasicDBObject("vital_signs.breathing_frecuency",
					new BasicDBObject("$gte", breathing_frecuency)));
			orQ.add(new BasicDBObject("vital_signs.heart_rate", new BasicDBObject("$gte", heart_rate)));
			orQuery.put("$or", orQ);

			//Se une la consulta que llevábamos, con el or multiple 

			List<BasicDBObject> obj1 = new ArrayList<BasicDBObject>();
			obj1.add(orQuery);
			obj1.add(andQuery1);
			andQuery2.put("$and", obj1);
		}
		
		//Se obtiene el cursor de la consulta final
		DBCursor dbc = collection.find(andQuery2);
		List<DBObject> objs = new ArrayList<DBObject>();
		
		//Se itera el cursor, y se agrega a una lista de DBObject, para retornarla. Esta lista tiene todos los documentos que 
		//cumplen con nuestras condiciones
		
		Iterator<DBObject> it = dbc.iterator();
	
		while (it.hasNext()) {
			objs.add(it.next());
		
			
		}
		
	

		return objs;
	}

}
