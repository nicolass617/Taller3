package com.edu.unbosque.model;

import java.util.List;
/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase que representa la capa de vista para geometry
 */
public class GeometryVO {

	private String type;
	private List<List<Double>> coordinates;

	//Getters and Setters de nuestra clase
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<List<Double>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<Double>> coordinates) {
		this.coordinates = coordinates;
	}

}
