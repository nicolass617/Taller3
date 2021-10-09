package com.edu.unbosque.connectiobd;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import redis.clients.jedis.Jedis;

public abstract class ConnectionBD {
	
	protected static final MongoClient mongoClient = new MongoClient("localhost", 27017);
	protected static final DB database = mongoClient.getDB("PPDBYBA");
	
	protected static final Jedis jedis = new Jedis("localhost", 6379);

}
