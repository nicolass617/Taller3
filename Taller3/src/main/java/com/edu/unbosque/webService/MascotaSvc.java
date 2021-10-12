package com.edu.unbosque.webService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.edu.unbosque.connectiobd.ConnectionBD;
import com.edu.unbosque.model.FeaturesVO;
import com.edu.unbosque.model.GeoJsonVO;
import com.edu.unbosque.model.GeometryVO;
import com.edu.unbosque.model.MascotaVO;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

@Path("/petsvc")
public class MascotaSvc extends ConnectionBD{
	
	private static final DBCollection collection = database.getCollection("Mascota");
	private static Gson gson = new Gson();
	
	@POST
	@Path("/addpet")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String addPet(MascotaVO m) {
		try {
			BasicDBObject doc = new BasicDBObject();
			
			m.setTimestamp(new Date());
			
			String obj = gson.toJson(m);
			
			doc = BasicDBObject.parse(obj);
			
			collection.insert(doc);
			
			String value = m.getGeolocation().getLatitude() + "," + m.getGeolocation().getLongitude() + "," + m.getTimestamp();
			jedis.lpush(m.getMicrochip(), value);
			jedis.expire(m.getMicrochip(), 3600);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "Se ha logrado guardar el registro";
	}
	
	@GET
	@Path("/getlocation")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public GeoJsonVO getLocations(MascotaVO m) {
		GeoJsonVO geo = new GeoJsonVO();
		try {
			long l = jedis.llen(m.getMicrochip());
			
			List<String> locations = jedis.lrange(m.getMicrochip(), 0, l);
			List<List<Double>> coordinates = new ArrayList<List<Double>>();
			List<FeaturesVO> features = new ArrayList<FeaturesVO>();
			
			geo.setType(jedis.get("type1"));
			
			FeaturesVO f = new FeaturesVO();
			f.setType(jedis.get("type2"));
			
			GeometryVO gm = new GeometryVO();
			gm.setType(jedis.get("type3"));
			
			
			for (int i = 0; i < locations.size(); i++) {
				String coord = locations.get(i);
				
				String[] strs = coord.split(",");
				
				List<Double> db = new ArrayList<Double>();
				
				db.add(Double.parseDouble(strs[0]));
				db.add(Double.parseDouble(strs[1]));
				
				coordinates.add(db);
				
				gm.setCoordinates(coordinates);
			}
			
			f.setGeometry(gm);
			
			features.add(f);
			
			geo.setFeatures(features);
		} catch (Exception e) {
			
		}
		return geo;
	}

}
