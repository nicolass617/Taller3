package com.edu.unbosque.model;

import java.util.List;

public class GeoJsonVO {

	private String type;
	private List<FeaturesVO> features;

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
