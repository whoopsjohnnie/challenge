package com.mesosphere.challenge.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.service.dao.EmbeddedStorageDAO;
import com.mesosphere.challenge.service.dao.IStorageDAO;

/**
 * 
 * @author john
 *
 *         This class provides Additional spring config outside of the regular
 *         properties files.
 *
 *         Spring provides many layers of configuration. Which ones you use is a
 *         matter of personal preference. hese three are used in this
 *         application:
 * 
 *         - src/main/resources/application.properties - Simple java properties
 *         file for key value pairs. Defines things like ports and paths.
 *
 *         - src/main/resources/applicationContext.xml - Defines java beans,
 *         their relationships, and links them together
 *
 *         - This class, which does the same as the applicationContext.xml file,
 *         except in java instead of XML. In this app the StorageServiceConfig
 *         has taken precedence over applicationContext.xml.
 *
 */
@Configuration
public class StorageServiceConfig {

	@Value("${com.mesosphere.challenge.service.storage.path}")
	private String storagePath;

	@Value("${com.mesosphere.challenge.service.storage.file}")
	private String storageFile;

	@Value("${com.mesosphere.challenge.service.storage.username}")
	private String storageUsername;

	@Value("${com.mesosphere.challenge.service.storage.password}")
	private String storagePassword;

	/**
	 * 
	 * Bean declaration. This defines the DAO to use in production. There is a
	 * similar declaration in the test config that applies to unit tests etc.
	 * 
	 * @return
	 * @throws StorageException
	 */
	@Bean
	public IStorageDAO storageDAO() throws StorageException {
		/*
		 * Return an EmbeddedStorageDAO configured to use file backed storage,
		 * so that we can restart the app without losing data.
		 */
		return new EmbeddedStorageDAO(storagePath, storageFile, storageUsername, storagePassword);
	}

}
