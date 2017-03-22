package com.mesosphere.challenge.service.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.model.StorageNode;
import com.mesosphere.challenge.service.dao.IStorageDAO;

/**
 * 
 * @author john
 *
 *         This is the main REST controller. It has a base mapping configured as
 *         /store and several REST handler endpoints, with and without extra
 *         path arguments.
 *
 */
@EnableWebMvc
@RestController
/*
 * Defining this seems to break my unit tests
 */
// @RequestMapping("/store")
public class StorageController {

	protected static final Logger logger = Logger.getLogger(StorageController.class);

	/*
	 * Index/list all case
	 */

	/**
	 * 
	 * This method handles GET calls to /store without any other path params,
	 * and returns a collection of nodes. This is the list, or index,
	 * controller.
	 * 
	 * 404 is returned if the storage DAO doesn't return an array. However no
	 * items found is usually handled by an empty arary, which would not result
	 * in a 404.
	 * 
	 * @param request
	 * @return
	 * @throws StorageException
	 */
	@RequestMapping(value = "/store", method = RequestMethod.GET)
	public ResponseEntity<Collection<StorageNode>> getBlobs(HttpServletRequest request) throws StorageException {
		logger.info("getBlobs");
		Collection<StorageNode> ret = this.storageConnection().getStorageNodes();
		if (ret == null) {
			return new ResponseEntity<Collection<StorageNode>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<StorageNode>>(ret, HttpStatus.OK);
	}

	/*
	 * Get either by location or by path
	 */

	/**
	 * 
	 * This method handles GET calls to /store/location and returns the node
	 * with that particular location.
	 * 
	 * 404 is returned if there is no node found with this location.
	 * 
	 * @param location
	 * @param request
	 * @return
	 * @throws StorageException
	 */
	@RequestMapping(value = "/store/{location}", method = RequestMethod.GET)
	public ResponseEntity<StorageNode> getBlobWithLocation(@PathVariable String location, HttpServletRequest request)
			throws StorageException {
		logger.info("getBlobWithLocation: " + location);
		StorageNode ret = this.storageConnection().getStorageNode(this.path(location));
		if (ret == null) {
			return new ResponseEntity<StorageNode>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<StorageNode>(ret, HttpStatus.OK);

	}

	/*
	 * Create either by location or by path
	 */

	/**
	 * 
	 * This call handles POST calls to /store/location. The call only allows
	 * POSTing if there is no node already associated with this location. The
	 * call is not idempotent in the sense that two repeated POSTs to the same
	 * call results in the same outcome. If that is the wish, then instead of
	 * returning BAD_REQUEST when a model alrady exists, simply return the model
	 * found.
	 * 
	 * Returns 400 if there already is a node with this location. Returns 404 if
	 * the DAO fails to store the node with this location (one could argue that
	 * this is the wrong code for this case).
	 * 
	 * @param location
	 * @param node
	 * @param request
	 * @return
	 * @throws StorageException
	 */
	@RequestMapping(value = "/store/{location}", method = RequestMethod.POST)
	public ResponseEntity<StorageNode> createBlobWithLocation(@PathVariable String location,
			@RequestBody StorageNode node, HttpServletRequest request) throws StorageException {
		logger.info("createBlobWithLocation");
		/*
		 * If node already exists, throw error
		 */
		if (this.storageConnection().getStorageNode(this.path(location)) != null) {
			return new ResponseEntity<StorageNode>(HttpStatus.BAD_REQUEST);
		}
		StorageNode ret = this.storageConnection().createStorageNode(node, this.path(location));
		if (ret == null) {
			return new ResponseEntity<StorageNode>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<StorageNode>(ret, HttpStatus.OK);
	}

	/*
	 * Update either by location or by path
	 */

	/**
	 * 
	 * This call handles PUT calls to /store/location. The call updates an
	 * existing node, and requires that a node exists with the location given to
	 * succeed.
	 * 
	 * Returns 404 if there is no node with this location already. Returns 404
	 * if the DAO fails to store the node with this location (one could argue
	 * that this is the wrong code for this case).
	 * 
	 * @param location
	 * @param node
	 * @param request
	 * @return
	 * @throws StorageException
	 */
	@RequestMapping(value = "/store/{location}", method = RequestMethod.PUT)
	public ResponseEntity<StorageNode> updateBlobWithLocation(@PathVariable String location,
			@RequestBody StorageNode node, HttpServletRequest request) throws StorageException {
		logger.info("updateBlobWithLocation");
		/*
		 * If node does not exist, throw 404
		 */
		if (this.storageConnection().getStorageNode(this.path(location)) == null) {
			return new ResponseEntity<StorageNode>(HttpStatus.NOT_FOUND);
		}
		StorageNode ret = this.storageConnection().updateStorageNode(node, this.path(location));
		if (ret == null) {
			return new ResponseEntity<StorageNode>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<StorageNode>(ret, HttpStatus.OK);
	}

	/*
	 * Delete either by location or by path
	 */

	/**
	 * 
	 * This call handles DELETE calls to /store/location. The call deletes an
	 * existing node, and requires that a node exists with the location given to
	 * succeed.
	 * 
	 * Returns 404 if there is no node with this location already.
	 * 
	 * @param location
	 * @param request
	 * @return
	 * @throws StorageException
	 */
	@RequestMapping(value = "/store/{location}", method = RequestMethod.DELETE)
	public ResponseEntity<StorageNode> deleteBlobWithLocation(@PathVariable String location, HttpServletRequest request)
			throws StorageException {
		logger.info("deleteBlobWithLocation");
		/*
		 * If node does not exist, throw 404
		 */
		if (this.storageConnection().getStorageNode(this.path(location)) == null) {
			return new ResponseEntity<StorageNode>(HttpStatus.NOT_FOUND);
		}
		this.storageConnection().deleteStorageNode(this.path(location));
		return new ResponseEntity<StorageNode>(HttpStatus.OK);
	}

	/*
	 * 
	 */

	/**
	 * 
	 * Convenience method
	 * 
	 * @param location
	 * @return
	 */
	protected Collection<String> path(String location) {
		return Arrays.asList(location);
	}

	/**
	 * 
	 * Convenience method
	 * 
	 * @param request
	 * @return
	 */
	protected Collection<String> path(HttpServletRequest request) {
		String path = request.getServletPath();
		return Arrays.asList(path.split(","));
	}

	@Autowired
	IStorageDAO storageDAO;

	protected IStorageDAO storageConnection() {
		return this.storageDAO;
	}

	public void setStorageConnection(IStorageDAO storageDAO) {
		this.storageDAO = storageDAO;
	}

}
