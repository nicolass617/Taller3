package com.edu.unbosque.webService;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.edu.unbosque.connectiobd.ConnectionBD;
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
	public void addPet(MascotaVO m) {
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
			e.printStackTrace();
		}
	}

}
