package com.mesosphere.challenge.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mesosphere.challenge.StorageException;
import com.mesosphere.challenge.service.dao.EmbeddedStorageDAO;
import com.mesosphere.challenge.service.dao.FileStorageDAO;
import com.mesosphere.challenge.service.dao.IStorageDAO;

/**
 * 
 * @author john
 *
 *         This class provides Additional spring config outside of the regular
 *         properties files.
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

	@Bean
	public IStorageDAO storageDAO() throws StorageException {
		return new EmbeddedStorageDAO(storagePath, storageFile, storageUsername, storagePassword);
	}

}
