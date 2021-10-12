package com.edu.unbosque.model;

/**
 * 
 * @author Nicolás Ávila, Sebastián Moncaleano, Diego Torres
 *Clase que representa la capa de vista para vital_sings 
 */
public class Vital_signsVO {

	private double temperature;
	private double heart_rate;
	private double breathing_frecuency;

	//Getters and setters de nuestra clase
	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getHeart_rate() {
		return heart_rate;
	}

	public void setHeart_rate(double heart_rate) {
		this.heart_rate = heart_rate;
	}

	public double getBreathing_frecuency() {
		return breathing_frecuency;
	}

	public void setBreathing_frecuency(double breathing_frecuency) {
		this.breathing_frecuency = breathing_frecuency;
	}

}
