package com.mesosphere.challenge.service.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author john
 *
 *         This class provides Additional spring config outside of the regular
 *         properties files.
 *
 */
@Configuration
public class StorageTestConfig {

	@Value("${com.mesosphere.challenge.service.storage.path}")
	private String storagePath;

}
