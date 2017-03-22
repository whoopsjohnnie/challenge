package com.mesosphere.challenge.model;

/**
 * 
 * @author john
 *
 */
public class StorageModel {

	String URI = null;

	/**
	 * 
	 */
	public StorageModel() {

	}

	public StorageModel(String URI) {
		this.URI = URI;
	}

	/**
	 * 
	 * @return
	 */
	public String getURI() {
		return this.URI;
	}

	/**
	 * 
	 * @param URI
	 */
	public void setURI(String URI) {
		this.URI = URI;
	}

}
