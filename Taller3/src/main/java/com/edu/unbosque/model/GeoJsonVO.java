package com.edu.unbosque.model;

import java.util.List;

/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase que representa la capa de vista para GEOJson
 */
public class GeoJsonVO {

	private String type;
	private List<FeaturesVO> features;

	
	//Getters and setters
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FeaturesVO> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeaturesVO> features) {
		this.features = features;
	}

}
