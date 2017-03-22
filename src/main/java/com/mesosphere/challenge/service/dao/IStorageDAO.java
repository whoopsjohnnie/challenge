package com.mesosphere.challenge.service.dao;

import java.util.Collection;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.model.StorageNode;

/**
 * 
 * @author john
 *
 *         This interface defines the DAO pattern in use. DAOs provide for a
 *         simple and flexible design where storage implementations can be
 *         swapped out easily via external config.
 *
 */
public interface IStorageDAO {

	/**
	 * 
	 * List all nodes within storage container
	 * 
	 * @return
	 * @throws StorageException
	 */
	Collection<StorageNode> getStorageNodes() throws StorageException;

	/**
	 * 
	 * Get node at path within storage container
	 * 
	 * @param path
	 * @return
	 * @throws StorageException
	 */
	StorageNode getStorageNode(Collection<String> path) throws StorageException;

	/**
	 * 
	 * Create node at path within storage container
	 * 
	 * @param node
	 * @param path
	 * @return
	 * @throws StorageException
	 */
	StorageNode createStorageNode(StorageNode node, Collection<String> path) throws StorageException;

	/**
	 * 
	 * Update node at path within storage container
	 * 
	 * @param node
	 * @param path
	 * @return
	 * @throws StorageException
	 */
	StorageNode updateStorageNode(StorageNode node, Collection<String> path) throws StorageException;

	/**
	 * 
	 * Delete node at path within storage container
	 * 
	 * @param path
	 * @throws StorageException
	 */
	void deleteStorageNode(Collection<String> path) throws StorageException;

}
