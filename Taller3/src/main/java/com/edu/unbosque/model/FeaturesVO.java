package com.edu.unbosque.model;

public class FeaturesVO {

	private String type;
	private PropertiesVO properties;
	private GeometryVO geometry;

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
