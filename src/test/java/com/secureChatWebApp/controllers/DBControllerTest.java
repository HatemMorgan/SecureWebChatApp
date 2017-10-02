package com.secureChatWebApp.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secureChatWebApp.configs.AppConfig;
import com.secureChatWebApp.daos.MessageDAO;
import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.models.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class DBControllerTest {

	private MockMvc mockMvc;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private MessageDAO messageDAO;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		addMessages();
	}

	@Test
	public void testDumpDB() throws Exception {
		MvcResult response = mockMvc.perform(get("/dbdump")).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("dbdump"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<LinkedHashMap<String, String>> messages = (List<LinkedHashMap<String, String>>) map.get("messages");

		assertEquals("Failure,Wrong Message fetched", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=",
				messages.get(0).get("text"));
		assertEquals("Failure,Wrong Message fetched", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=",
				messages.get(1).get("text"));
		assertEquals("Failure,Wrong Message fetched", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=",
				messages.get(2).get("text"));
		assertEquals("Failure,Wrong Message fetched", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=",
				messages.get(3).get("text"));
		assertEquals("Failure,Wrong Message fetched", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=",
				messages.get(4).get("text"));
		assertEquals("Failure,Wrong Message fetched", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=",
				messages.get(5).get("text"));

	}

	@After
	public void tearDown() {
		messageDAO.deleteMessage("test1", "test2");
		userDAO.deleteUser("test1");
		userDAO.deleteUser("test2");

	}

	private void addMessages() throws InterruptedException, DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageDAO.createMessage("test1", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");
		messageDAO.createMessage("test2", "test1", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		messageDAO.createMessage("test2", "test1", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

	}

}
