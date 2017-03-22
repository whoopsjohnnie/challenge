package com.mesosphere.challenge.service.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.util.StringUtils;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.model.StorageNode;

/**
 * 
 * @author john
 *
 *         This is a DAO implementation class that stores nodes in an embedded
 *         DB.
 *
 */
public class EmbeddedStorageDAO implements IStorageDAO {

	protected static final Logger logger = Logger.getLogger(EmbeddedStorageDAO.class);

	private String storagePath = null;
	private String storageFile = null;
	private String storageUsername = null;
	private String storagePassword = null;

	Connection connection = null;

	/**
	 * 
	 * @param storagePath
	 * @param storageFile
	 * @param storageUsername
	 * @param storagePassword
	 * @throws StorageException
	 */
	public EmbeddedStorageDAO() throws StorageException {
		this.storagePath = null;
		this.storageFile = null;
		this.storageUsername = null;
		this.storagePassword = null;
		this.init();
	}

	/**
	 * 
	 * @param storagePath
	 * @param storageFile
	 * @param storageUsername
	 * @param storagePassword
	 * @throws StorageException
	 */
	public EmbeddedStorageDAO(String storagePath, String storageFile, String storageUsername, String storagePassword)
			throws StorageException {
		this.storagePath = storagePath;
		this.storageFile = storageFile;
		this.storageUsername = storageUsername;
		this.storagePassword = storagePassword;
		this.init();
	}

	/**
	 * 
	 * @throws StorageException
	 */
	protected void init() throws StorageException {
		logger.info("EmbeddedStorageDAO init with: ");
		if (this.storagePath != null) {
			logger.info(" -- storagePath: " + this.storagePath);
			logger.info(" -- storageFile: " + this.storageFile);
			logger.info(" -- storageUsername: " + this.storageUsername);
		}

		try {
			JdbcDataSource ds = new JdbcDataSource();
			if (this.storagePath == null) {
				// jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
				ds.setURL("jdbc:h2:mem:challengedb;DB_CLOSE_DELAY=-1");
				Connection conn = ds.getConnection();
				this.connection = conn;
			} else {
				// jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE
				ds.setURL("jdbc:h2:file:" + this.storagePath + "/" + this.storageFile + ";DB_CLOSE_ON_EXIT=FALSE");
				ds.setUser(this.storageUsername);
				ds.setPassword(this.storagePassword);
				Connection conn = ds.getConnection();
				this.connection = conn;
			}

			Statement s = this.connection.createStatement();
			s.execute("CREATE TABLE IF NOT EXISTS STORAGE_NODE (LOCATION VARCHAR(1024), CONTENTS BLOB)");

		} catch (SQLException e) {
			throw new StorageException("EmbeddedStorageDAO failed to initialize", e);
		} finally {
			//
		}

	}

	protected String path(Collection<String> path) {
		return StringUtils.arrayToDelimitedString(path.toArray(), "/");
	}

	@Override
	public Collection<StorageNode> getStorageNodes() throws StorageException {
		logger.info("getStorageNodes");

		try {
			if ((this.connection == null) || (this.connection.isClosed())) {
				throw new StorageException("Embedded DB connection is closed");
			}
		} catch (SQLException e) {
			throw new StorageException("Embedded DB connection is closed", e);
		}

		try {

			/*
			 * !! The below is vulnerable to SQL injection hacks !!
			 */
			// Statement statement = this.connection.createStatement();
			// ResultSet result = statement.executeQuery("SELECT * FROM
			// STORAGE_NODE;");

			/*
			 * The below is not vulnerable to SQL injection hacks
			 */
			String sql = "SELECT * FROM STORAGE_NODE;";
			PreparedStatement statement = this.connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();

			Collection<StorageNode> ret = new ArrayList<StorageNode>();
			if (result != null) {
				while (result.next()) {
					StorageNode node = new StorageNode();
					node.setLocation(result.getString(1));
					StringBuilder inputStringBuilder = new StringBuilder();
					InputStream stream = result.getBinaryStream(2);
					if (stream != null) {
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(stream, StandardCharsets.UTF_8));
						String line = bufferedReader.readLine();
						while (line != null) {
							inputStringBuilder.append(line);
							line = bufferedReader.readLine();
						}
						node.setContents(inputStringBuilder.toString());
					}
					ret.add(node);
				}
			}
			return ret;
		} catch (SQLException e) {
			throw new StorageException("Embedded DB query failed", e);
		} catch (IOException e) {
			throw new StorageException("Embedded DB query failed", e);
		}

	}

	@Override
	public StorageNode getStorageNode(Collection<String> path) throws StorageException {
		logger.info("getStorageNode");

		if (path == null) {
			throw new StorageException("Path is null");
		}

		try {
			if ((this.connection == null) || (this.connection.isClosed())) {
				throw new StorageException("Embedded DB connection is closed");
			}
		} catch (SQLException e) {
			throw new StorageException("Embedded DB connection is closed", e);
		}

		try {

			/*
			 * !! The below is vulnerable to SQL injection hacks !!
			 */
			// Statement statement = this.connection.createStatement();
			// ResultSet result = statement
			// .executeQuery("SELECT * FROM STORAGE_NODE WHERE LOCATION = '" +
			// this.path(path) + "';");

			/*
			 * The below is not vulnerable to SQL injection hacks
			 */
			String sql = "SELECT * FROM STORAGE_NODE WHERE LOCATION = ?;";
			PreparedStatement statement = this.connection.prepareStatement(sql);
			statement.setString(1, this.path(path));
			ResultSet result = statement.executeQuery();

			if ((result != null) && (result.first())) {
				StorageNode node = new StorageNode();
				node.setLocation(result.getString(1));
				StringBuilder inputStringBuilder = new StringBuilder();
				InputStream stream = result.getBinaryStream(2);
				if (stream != null) {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(stream, StandardCharsets.UTF_8));
					String line = bufferedReader.readLine();
					while (line != null) {
						inputStringBuilder.append(line);
						line = bufferedReader.readLine();
					}
					node.setContents(inputStringBuilder.toString());
				}
				return node;
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new StorageException("Embedded DB query failed", e);
		} catch (IOException e) {
			throw new StorageException("Embedded DB query failed", e);
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

		try {
			/*
			 * The below is not vulnerable to SQL injection hacks
			 */
			String sql = "INSERT INTO STORAGE_NODE VALUES ( ?, ? );";
			PreparedStatement statement = this.connection.prepareStatement(sql);
			if (node.getContents() == null) {
				statement.setString(1, this.path(path));
				statement.setNull(2, Types.BLOB);
			} else {
				statement.setString(1, this.path(path));
				InputStream stream = new ByteArrayInputStream(node.getContents().getBytes(StandardCharsets.UTF_8));
				statement.setBinaryStream(2, stream);
			}
			statement.executeUpdate();
			return node;
		} catch (SQLException e) {
			throw new StorageException("Embedded DB query failed", e);
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

		try {
			/*
			 * The below is not vulnerable to SQL injection hacks
			 */
			String sql = "UPDATE STORAGE_NODE SET LOCATION = ?, CONTENTS = ? WHERE LOCATION = ?;";
			PreparedStatement statement = this.connection.prepareStatement(sql);
			if (node.getContents() == null) {
				statement.setString(1, this.path(path));
				statement.setNull(2, Types.BLOB);
				statement.setString(3, this.path(path));
			} else {
				statement.setString(1, this.path(path));
				InputStream stream = new ByteArrayInputStream(node.getContents().getBytes(StandardCharsets.UTF_8));
				statement.setBinaryStream(2, stream);
				statement.setString(3, this.path(path));
			}
			statement.executeUpdate();
			return node;
		} catch (SQLException e) {
			throw new StorageException("Embedded DB query failed", e);
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

		try {
			/*
			 * The below is not vulnerable to SQL injection hacks
			 */
			String sql = "DELETE FROM STORAGE_NODE WHERE LOCATION = ?;";
			PreparedStatement statement = this.connection.prepareStatement(sql);
			statement.setString(1, this.path(path));
			statement.execute();
		} catch (SQLException e) {
			throw new StorageException("Embedded DB query failed", e);
		}

	}

}
