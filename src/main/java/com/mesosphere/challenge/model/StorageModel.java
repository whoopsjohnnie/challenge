package com.mesosphere.challenge.model;

/**
 * 
 * @author john
 *
 *         This class forms the basis of all storage models. It provides any and
 *         all common fields and common de/serialization methods.
 *
 *         Right now it only has one field, URI. This field should provide a
 *         link to REST API endpoint handling this model. With this field one
 *         can navigate from something like a list or collection view to the
 *         view that provides LCM/CRUD operations for this model.
 *
 */
public class StorageModel {

	/*
	 * Should be filled out with a link, absolute or relative, to the REST
	 * endpoint handling this model.
	 */
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
