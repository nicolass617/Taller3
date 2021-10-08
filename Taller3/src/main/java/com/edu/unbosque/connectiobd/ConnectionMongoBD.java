package com.edu.unbosque.connectiobd;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class ConnectionMongoBD {
	
	public static final MongoClient mongoClient = new MongoClient("localhost", 27017);
	public static final DB database = mongoClient.getDB("PPDBYBA");

}
