package com.edu.unbosque.webService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;

import com.edu.unbosque.connectiobd.ConnectionMongoBD;
import com.edu.unbosque.model.MascotaVO;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

@Path("/petsvc")
public class MascotaSvc extends ConnectionMongoBD{
	
	private static final DBCollection collection = database.getCollection("Mascota");
	private static Gson gson = new Gson();
	
	@POST
	@Path("/addpet")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public void addPet(MascotaVO m) {
		try {
			BasicDBObject doc = new BasicDBObject();
			
			String obj = gson.toJson(m);
			
			 doc = BasicDBObject.parse(obj);
			
			collection.insert(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
