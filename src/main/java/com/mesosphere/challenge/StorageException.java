package com.mesosphere.challenge;

/**
 * 
 * @author john
 *
 *         This is a generic checked exception that we can use throughout the
 *         storage service.
 *
 */
public class StorageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2405000991592210923L;

	/**
	 * 
	 * @param message
	 * @param exception
	 */
	public StorageException(String message, Exception exception) {
		super(message, exception);
	}

	/**
	 * 
	 * @param exception
	 */
	public StorageException(Exception exception) {
		super(exception);
	}

	/**
	 * 
	 * @param message
	 */
	public StorageException(String message) {
		super(message);
	}

}
