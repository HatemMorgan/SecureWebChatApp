package com.secureChatWebApp.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.secureChatWebApp.configs.AppConfig;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Reference see end of this tutorial
 * http://spring.io/guides/tutorials/bookmarks/
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class PublicKeysControllerTest {


	private MockMvc mockMvc;
	 private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
	            MediaType.APPLICATION_JSON.getSubtype(),
	            Charset.forName("utf8"));
	@Autowired
	private PublicKeysService publicKeysService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testGetServerPublicKey() throws Exception {
		mockMvc.perform(get("/publicKey")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.encryptionPubKey", is(publicKeysService.getEncryptionPublicKey())))
				.andExpect(jsonPath("$.signaturePubKey", is(publicKeysService.getSignaturePublicKey())));
	}
	
	@Test
	public void testGetSignaturePublicKey() throws Exception {
		mockMvc.perform(get("/publicKey")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.signaturePubKey", is(publicKeysService.getSignaturePublicKey())));
	}
	
	
	@Test
	public void testGetEncryptionPublicKey() throws Exception {
		mockMvc.perform(get("/publicKey")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.encryptionPubKey", is(publicKeysService.getEncryptionPublicKey())));
	}

	// @Test
	// public void readSingleBookmark() throws Exception {
	// mockMvc.perform(get("/" + userName + "/bookmarks/" +
	// this.bookmarkList.get(0).getId()))
	// .andExpect(status().isOk()).andExpect(content().contentType(contentType))
	// .andExpect(jsonPath("$.id",
	// is(this.bookmarkList.get(0).getId().intValue())))
	// .andExpect(jsonPath("$.uri", is("http://bookmark.com/1/" + userName)))
	// .andExpect(jsonPath("$.description", is("A description")));
	// }

	// @Test
	// public void readBookmarks() throws Exception {
	// mockMvc.perform(get("/" + userName +
	// "/bookmarks")).andExpect(status().isOk())
	// .andExpect(content().contentType(contentType)).andExpect(jsonPath("$",
	// hasSize(2)))
	// .andExpect(jsonPath("$[0].id",
	// is(this.bookmarkList.get(0).getId().intValue())))
	// .andExpect(jsonPath("$[0].uri", is("http://bookmark.com/1/" + userName)))
	// .andExpect(jsonPath("$[0].description", is("A description")))
	// .andExpect(jsonPath("$[1].id",
	// is(this.bookmarkList.get(1).getId().intValue())))
	// .andExpect(jsonPath("$[1].uri", is("http://bookmark.com/2/" + userName)))
	// .andExpect(jsonPath("$[1].description", is("A description")));
	// }

	// @Test
	// public void createBookmark() throws Exception {
	// String bookmarkJson = json(new Bookmark(this.account, "http://spring.io",
	// "a bookmark to the best resource for Spring news and information"));
	//
	// this.mockMvc.perform(post("/" + userName +
	// "/bookmarks").contentType(contentType).content(bookmarkJson))
	// .andExpect(status().isCreated());
	// }

	// protected String json(Object o) throws IOException {
	// MockHttpOutputMessage mockHttpOutputMessage = new
	// MockHttpOutputMessage();
	// this.mappingJackson2HttpMessageConverter.write(o,
	// MediaType.APPLICATION_JSON, mockHttpOutputMessage);
	// return mockHttpOutputMessage.getBodyAsString();
	// }
}
