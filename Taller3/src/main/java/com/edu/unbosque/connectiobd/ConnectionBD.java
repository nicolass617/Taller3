package com.edu.unbosque.connectiobd;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import redis.clients.jedis.Jedis;
/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase abstracta que nos sirve para instanciar en otras clases los protocolos de conexión según sea necesario
 */
public abstract class ConnectionBD {
	
	//Variables para la conexión con MongoDB
	protected static final MongoClient mongoClient = new MongoClient("localhost", 27017);
	protected static final DB database = mongoClient.getDB("PPDBYBA");
	
	//Variables para la conexión con Redis
	protected static final Jedis jedis = new Jedis("localhost", 6379);

}
