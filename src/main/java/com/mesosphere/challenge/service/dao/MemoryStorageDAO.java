package com.mesosphere.challenge.service.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.model.StorageNode;

/**
 * 
 * @author john
 *
 *         This is a DAO implementation class that stores nodes in memory.
 *
 */
public class MemoryStorageDAO implements IStorageDAO {

	protected static final Logger logger = Logger.getLogger(MemoryStorageDAO.class);

	/*
	 * Simple map to keep nodes in memory
	 */
	private Map<String, StorageNode> backing = new HashMap<String, StorageNode>();

	protected String path(Collection<String> path) {
		return StringUtils.arrayToDelimitedString(path.toArray(), "/");
	}

	@Override
	public Collection<StorageNode> getStorageNodes() throws StorageException {
		logger.info("getStorageNodes");

		if (this.backing.values() == null) {
			return Arrays.asList();
		}

		return this.backing.values();
	}

	@Override
	public StorageNode getStorageNode(Collection<String> path) throws StorageException {
		logger.info("getStorageNode");

		if (path == null) {
			throw new StorageException("Path is null");
		}

		if (this.backing.containsKey(this.path(path)) == false) {
			return null;
		}

		return this.backing.get(this.path(path));
	}

	@Override
	public StorageNode createStorageNode(StorageNode node, Collection<String> path) throws StorageException {
		logger.info("createStorageNode");

		if (node == null) {
			throw new StorageException("Node is null");
		}

		if (path == null) {
			throw new StorageException("Path is null");
		}

		this.backing.put(this.path(path), node);

		return this.backing.get(this.path(path));
	}

	@Override
	public StorageNode updateStorageNode(StorageNode node, Collection<String> path) throws StorageException {
		logger.info("updateStorageNode");

		if (node == null) {
			throw new StorageException("Node is null");
		}

		if (path == null) {
			throw new StorageException("Path is null");
		}

		if (this.backing.containsKey(this.path(path)) == false) {
			return null;
		}

		this.backing.remove(this.path(path));
		this.backing.put(this.path(path), node);

		return this.backing.get(this.path(path));
	}

	@Override
	public void deleteStorageNode(Collection<String> path) throws StorageException {
		logger.info("deleteStorageNode");

		if (path == null) {
			throw new StorageException("Path is null");
		}

		if (path.size() != 1) {
			throw new StorageException("Path is longer than one element, currently not supported");
		}

		if (this.backing.containsKey(this.path(path)) == false) {
			return;
		}

		this.backing.remove(this.path(path));
	}

}
