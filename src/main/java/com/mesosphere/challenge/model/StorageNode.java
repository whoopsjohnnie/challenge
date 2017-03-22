package com.mesosphere.challenge.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * @author john
 *
 *         This class provide a model definition for storage blobs.
 *
 */
/*
 * Clean up JSON
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageNode extends StorageModel {

	/*
	 * Field holding the location of this file
	 */
	String location = null;

	/*
	 * Field holding the file contents of this file, backed by a DB blob (binary
	 * large object) so can hold quite a large amount of data.
	 */
	String contents = null;

	/*
	 * Field holding the content length of the contents of this file., i.e.
	 * number of UTF-8 characters.
	 */
	Integer length = null;

	/**
	 * 
	 */
	public StorageNode() {

	}

	/**
	 * 
	 * @param contents
	 */
	public StorageNode(String contents) {
		if (contents != null) {
			this.contents = contents;
			this.length = new Integer(contents.length());
		}
	}

	/**
	 * 
	 * @param contents
	 */
	public StorageNode(byte[] contents) {
		this.contents = new String(contents);
		this.length = new Integer(contents.length);
	}

	/**
	 * 
	 * @param location
	 * @param contents
	 */
	public StorageNode(String location, String contents) {
		this.location = location;
		this.URI = "/store/" + location;
		if (contents != null) {
			this.contents = contents;
			this.length = new Integer(contents.length());
		}
	}

	/**
	 * 
	 * @param location
	 * @param contents
	 */
	public StorageNode(String location, byte[] contents) {
		this.location = location;
		this.URI = "/store/" + location;
		if (contents != null) {
			this.contents = new String(contents);
			this.length = contents.length;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * 
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
		this.URI = "/store/" + location;
	}

	/**
	 * 
	 * @return
	 */
	public String getContents() {
		return this.contents;
	}

	/**
	 * 
	 * @param contents
	 */
	public void setContents(String contents) {
		if (contents != null) {
			this.contents = contents;
			this.length = new Integer(contents.length());
		} else {
			this.contents = null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Integer getLength() {
		return this.length;
	}

	/**
	 * 
	 * @param length
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

}
