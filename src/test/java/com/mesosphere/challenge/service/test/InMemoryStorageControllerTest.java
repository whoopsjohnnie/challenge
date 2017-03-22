package com.mesosphere.challenge.service.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mesosphere.challenge.model.StorageNode;
import com.mesosphere.challenge.service.controller.StorageController;
import com.mesosphere.challenge.service.dao.MemoryStorageDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:applicationContext.xml" })
@Import(StorageTestConfig.class)
public class InMemoryStorageControllerTest {

	@InjectMocks
	private StorageController storageController;

	@InjectMocks
	private MemoryStorageDAO memoryStorageDAO;

	private MockMvc mockMvc;

	@Before
	public void setup() {

		// Process mock annotations
		MockitoAnnotations.initMocks(this);

		storageController.setStorageConnection(memoryStorageDAO);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();

	}

	@Test
	public void testInvalidURL() throws Exception {

		/*
		 * No such URL, expect 404
		 */
		this.mockMvc.perform(get("")).andExpect(status().isNotFound());

	}

	@Test
	public void testIndexEmptyContainer() throws Exception {

		/*
		 * Empty storage container, should return empty list with 200
		 */
		this.mockMvc.perform(get("/store")).andExpect(status().isOk());

	}

	@Test
	public void testIndexEmptyContainerNoSuchFile() throws Exception {

		/*
		 * Empty storage container, no such blob, return 404
		 */
		this.mockMvc.perform(get("/store/doesnotexist")).andExpect(status().isNotFound());

	}

	@Test
	public void testCreateNewBlobNoBody() throws Exception {

		/*
		 * No body attached
		 */
		this.mockMvc.perform(post("/store/createblob")).andExpect(status().is4xxClientError());

	}

	@Test
	public void testCreateNewBlobWithInvalidBody() throws Exception {

		String body = "invalidjson";

		/*
		 * Invalid body attached
		 */
		this.mockMvc.perform(post("/store/createblob").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().is4xxClientError());

	}

	/**
	 * 
	 * This test runs a pretty complete create test. Note that it performs other
	 * related operations after the create task to make sure the node really
	 * exists and works. Also note that we run a second create after deleting
	 * the node, to ensure that we do not have a corner case where we cannot
	 * recreate nodes that were previously deleted.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateNewBlobWithBody() throws Exception {

		/*
		 * 
		 */
		this.mockMvc.perform(get("/store/createblob")).andExpect(status().isNotFound());

		ObjectMapper mapper = new ObjectMapper();
		StorageNode node = new StorageNode("hello createblob");
		String body = mapper.writeValueAsString(node);

		/*
		 * Create blob
		 */
		this.mockMvc.perform(post("/store/createblob").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk());

		/*
		 * Get blob, make sure it is really there
		 */
		this.mockMvc.perform(get("/store/createblob")).andExpect(status().isOk());

		/*
		 * Cannot create again
		 */
		this.mockMvc.perform(post("/store/createblob").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().is4xxClientError());

		/*
		 * Delete blob
		 */
		this.mockMvc.perform(delete("/store/createblob")).andExpect(status().isOk());

		/*
		 * Get blob again, make sure it is not found
		 */
		this.mockMvc.perform(get("/store/createblob")).andExpect(status().isNotFound());

		/*
		 * Create blob again, make sure that we can create again after the
		 * delete
		 */
		this.mockMvc.perform(post("/store/createblob").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk());

		/*
		 * Get blob, make sure it is really there
		 */
		this.mockMvc.perform(get("/store/createblob")).andExpect(status().isOk());

		/*
		 * Delete blob
		 */
		this.mockMvc.perform(delete("/store/createblob")).andExpect(status().isOk());

		/*
		 * Get blob again, make sure it is not found
		 */
		this.mockMvc.perform(get("/store/createblob")).andExpect(status().isNotFound());

	}

	@Test
	public void testUpdateBlobNoBody() throws Exception {

		/*
		 * No body takes presedence over the 404 not found
		 */
		this.mockMvc.perform(put("/store/doesnotexist")).andExpect(status().is4xxClientError());

	}

	@Test
	public void testUpdateBlobInvalidBody() throws Exception {

		String body = "invalidjson";

		/*
		 * Invalid body takes presedence over the 404 not found
		 */
		this.mockMvc.perform(put("/store/doesnotexist").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().is4xxClientError());

	}

	@Test
	public void testUpdateBlobNotFound() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		StorageNode node = new StorageNode("hello updateblob");
		String body = mapper.writeValueAsString(node);

		/*
		 * When we have a proper body present, the 404 should show
		 */
		this.mockMvc.perform(put("/store/doesnotexist").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isNotFound());

	}

	/**
	 * 
	 * This test runs a pretty complete update test. Note that it performs other
	 * related operations after the create and update tasks to make sure the
	 * node really exists and works.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateNewBlobWithBody() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		StorageNode node = new StorageNode("hello updateblob");
		String body = mapper.writeValueAsString(node);

		/*
		 * Create blob
		 */
		this.mockMvc.perform(post("/store/updateblob").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk());

		/*
		 * Get blob, make sure it is really there
		 */
		this.mockMvc.perform(get("/store/updateblob")).andExpect(status().isOk());

		/*
		 * 
		 */
		StorageNode node2 = new StorageNode("hello updateblob, with new contents");
		String body2 = mapper.writeValueAsString(node2);

		/*
		 * Update the blob
		 */
		this.mockMvc.perform(put("/store/updateblob").contentType(MediaType.APPLICATION_JSON).content(body2))
				.andExpect(status().isOk());

		/*
		 * Get blob, make sure it is really there
		 */
		this.mockMvc.perform(get("/store/updateblob")).andExpect(status().isOk());

		/*
		 * Delete blob
		 */
		this.mockMvc.perform(delete("/store/updateblob")).andExpect(status().isOk());

		/*
		 * Get blob again, make sure it is not found
		 */
		this.mockMvc.perform(get("/store/updateblob")).andExpect(status().isNotFound());

	}

	@Test
	public void testDeleteBlobNotFound() throws Exception {

		/*
		 * When we have a proper body present, the 404 should show
		 */
		this.mockMvc.perform(delete("/store/doesnotexist")).andExpect(status().isNotFound());

	}

	@Test
	public void testDeleteBlob() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		StorageNode node = new StorageNode("hello deleteblob");
		String body = mapper.writeValueAsString(node);

		/*
		 * Create blob
		 */
		this.mockMvc.perform(post("/store/deleteblob").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isOk());

		/*
		 * Delete blob
		 */
		this.mockMvc.perform(delete("/store/deleteblob")).andExpect(status().isOk());

		/*
		 * Delete blob again, make sure it is not there
		 */
		this.mockMvc.perform(delete("/store/deleteblob")).andExpect(status().isNotFound());

	}

}
