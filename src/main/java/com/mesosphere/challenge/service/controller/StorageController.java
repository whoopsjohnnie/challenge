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

	@RequestMapping(value = "/store/{location}", method = RequestMethod.POST)
	public ResponseEntity<StorageNode> createBlobWithLocation(@PathVariable String location, @RequestBody StorageNode node,
			HttpServletRequest request) throws StorageException {
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

	@RequestMapping(value = "/store/{location}", method = RequestMethod.PUT)
	public ResponseEntity<StorageNode> updateBlobWithLocation(@PathVariable String location, @RequestBody StorageNode node,
			HttpServletRequest request) throws StorageException {
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
