package com.edu.unbosque.model;

/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase que representa la capa de vista para features de GEOJson
 */
public class FeaturesVO {

	private String type;
	private PropertiesVO properties;
	private GeometryVO geometry;

	//Getter y setters de nuestra clase
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PropertiesVO getProperties() {
		return properties;
	}

	public void setProperties(PropertiesVO properties) {
		this.properties = properties;
	}

	public GeometryVO getGeometry() {
		return geometry;
	}

	public void setGeometry(GeometryVO geometry) {
		this.geometry = geometry;
	}

}
