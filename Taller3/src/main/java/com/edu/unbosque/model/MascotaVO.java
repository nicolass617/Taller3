package com.edu.unbosque.model;

import java.util.Date;
import java.util.List;

public class MascotaVO {

	private String _id;
	private Date timestamp;
	private String microchip;
	private String pet_name;
	private String owner_name;
	private String species;
	private List<GeolocationVO> geolocation;
	private List<Vital_signsVO> vital_signs;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMicrochip() {
		return microchip;
	}

	public void setMicrochip(String microchip) {
		this.microchip = microchip;
	}

	public String getPet_name() {
		return pet_name;
	}

	public void setPet_name(String pet_name) {
		this.pet_name = pet_name;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public List<GeolocationVO> getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(List<GeolocationVO> geolocation) {
		this.geolocation = geolocation;
	}

	public List<Vital_signsVO> getVital_signs() {
		return vital_signs;
	}

	public void setVital_signs(List<Vital_signsVO> vital_signs) {
		this.vital_signs = vital_signs;
	}

}
