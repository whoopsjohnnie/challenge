package com.mesosphere.challenge.service.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.model.StorageNode;

/**
 * 
 * @author john
 *
 *         This is a DAO implementation class that stores nodes on disk.
 *
 */
public class FileStorageDAO implements IStorageDAO {

	protected static final Logger logger = Logger.getLogger(FileStorageDAO.class);

	protected static final String CHALLENGEROOT = "challengeroot";

	String root = null;
	Path rootFolder = null;

	/*
	 * Constructor
	 */
	public FileStorageDAO() throws StorageException {
		this.root = "/tmp/" + CHALLENGEROOT;
		this.init();
	}

	/*
	 * Constructor
	 */
	public FileStorageDAO(String root) throws StorageException {
		/*
		 * Make sure we create a subfolder so that we don't accidentally delete
		 * any preexisting content.
		 */
		this.root = root + "/" + CHALLENGEROOT;
		this.init();
	}

	protected void init() throws StorageException {
		logger.info("FileStorageDAO init at: " + this.root);

		try {
			Path rootFolder = Paths.get(this.root);
			if (Files.notExists(rootFolder)) {
				logger.info("FileStorageDAO init at: " + this.root + ", creating storage folder");
				Files.createDirectory(rootFolder);
			}
			if ((Files.exists(rootFolder)) && (Files.isDirectory(rootFolder) == false)) {
				throw new StorageException("FileStorageDAO failed to initialize, root folders is not a folder");
			}
			this.rootFolder = rootFolder;
			logger.info("FileStorageDAO init at: " + this.root + ", initialization succeeded");
		} catch (IOException e) {
			throw new StorageException("FileStorageDAO failed to initialize", e);
		} finally {
			// Cleanup here
		}

	}

	@Override
	public Collection<StorageNode> getStorageNodes() throws StorageException {
		logger.info("getStorageNodes");

		Path filePath = Paths.get(this.root);
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(filePath)) {
			Collection<StorageNode> ret = new ArrayList<StorageNode>();
			for (Path path : directoryStream) {
				ret.add(new StorageNode(path.getFileName().toString(), (String) null));
			}
			return ret;
		} catch (IOException e) {
			throw new StorageException("getStorageNodes failed with java IOException", e);
		} finally {
			// clean up if needed
		}

	}

	@Override
	public StorageNode getStorageNode(Collection<String> path) throws StorageException {
		logger.info("getStorageNode");

		if (path == null) {
			throw new StorageException("Path is null");
		}

		if (path.size() != 1) {
			throw new StorageException("Path is longer than one element, currently not supported");
		}

		/*
		 * Get filename, we only support single filenames for now
		 */
		String name = path.iterator().next();

		try {
			Path filePath = Paths.get(this.root + "/" + name);
			/*
			 * Make sure file exists
			 */
			if (Files.exists(filePath) == false) {
				return null;
			}
			byte[] read = Files.readAllBytes(filePath);
			return new StorageNode(name, read);
		} catch (IOException e) {
			throw new StorageException("getStorageNode failed with java IOException", e);
		} finally {
			// clean up if needed
		}

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

		if (path.size() != 1) {
			throw new StorageException("Path is longer than one element, currently not supported");
		}

		/*
		 * Get filename, we only support single filenames for now
		 */
		String name = path.iterator().next();

		try {

			Path filePath = Paths.get(this.root + "/" + name);

			/*
			 * Make sure file does not exist
			 */
			if (Files.exists(filePath) == true) {
				return null;
			}

			/*
			 * 
			 */
			Files.createFile(filePath);

			/*
			 * Make sure we what we write is sane
			 */
			if (node.getContents() != null) {
				Files.write(filePath, node.getContents().getBytes());
			} else {
				Files.write(filePath, "".getBytes());
			}

			/*
			 * Make sure file exists
			 */
			if (Files.exists(filePath) == false) {
				return null;
			}
			byte[] read = Files.readAllBytes(filePath);
			return new StorageNode(name, read);

		} catch (IOException e) {
			throw new StorageException("createStorageNode failed with java IOException", e);
		} finally {
			// clean up if needed
		}

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

		if (path.size() != 1) {
			throw new StorageException("Path is longer than one element, currently not supported");
		}

		/*
		 * Get filename, we only support single filenames for now
		 */
		String name = path.iterator().next();

		try {

			Path filePath = Paths.get(this.root + "/" + name);

			/*
			 * Make sure file does not exist
			 */
			if (Files.exists(filePath) != true) {
				return null;
			}

			/*
			 * Make sure we what we write is sane
			 */
			if (node.getContents() != null) {
				Files.write(filePath, node.getContents().getBytes());
			} else {
				Files.write(filePath, "".getBytes());
			}

			/*
			 * Make sure file exists
			 */
			if (Files.exists(filePath) == false) {
				return null;
			}

			byte[] read = Files.readAllBytes(filePath);
			return new StorageNode(name, read);

		} catch (IOException e) {
			throw new StorageException("updateStorageNode failed with java IOException", e);
		} finally {
			// clean up if needed
		}

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

		/*
		 * Get filename, we only support single filenames for now
		 */
		String name = path.iterator().next();

		try {

			Path filePath = Paths.get(this.root + "/" + name);

			/*
			 * Make sure file does not exist
			 */
			if (Files.exists(filePath) != true) {
				return;
			}

			Files.delete(filePath);
			return;

		} catch (IOException e) {
			throw new StorageException("deleteStorageNode failed with java IOException", e);
		} finally {
			// clean up if needed
		}

	}

}
