package com.edu.unbosque.model;

/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase que representa la capa de vista para geolocation
 */
public class GeolocationVO {

	private double latitude;
	private double longitude;

	//Getters and Setters de nuestra clase
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
