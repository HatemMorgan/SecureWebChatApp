package com.secureChatWebApp.controllers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Signature;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FlashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secureChatWebApp.configs.AppConfig;
import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.HashUtility;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class RegisterationControllerTest {
	private MockMvc mockMvc;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserDAO userDAO;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testRegisteration() throws Exception {
		// request public key of server
		MvcResult response = mockMvc.perform(get("/publicKey")).andExpect(status().isOk()).andReturn();
		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});
		// System.out.println(map);
		String pubKeyEnc = map.get("encryptionPubKey");

		PublicKey pubKey = RSAUtility.reConstructPublicKey(pubKeyEnc);

		// create a new client
		String userName = "test123";
		String hashedPassword = HashUtility.hashSHA1("1234");

		RSAPublicKey rsaEncPubKey = ((RSAPublicKey) RSAUtility.generatetKeyPair().getPublic());
		// key parameters must be sent in hexadecimal format
		String encPubKeyStr = String.format("%040x", rsaEncPubKey.getModulus()) + ":" + String.format("%040x", rsaEncPubKey.getPublicExponent());

		KeyPair rsaSignKeyPair = RSAUtility.generatetKeyPair();
		RSAPublicKey rsaSignPubKey = ((RSAPublicKey) rsaSignKeyPair.getPublic());
		// key parameters must be sent in hexadecimal format
		String signPubKeyStr =  String.format("%040x", rsaSignPubKey.getModulus()) + ":" + String.format("%040x", rsaSignPubKey.getPublicExponent());

		String data = hashedPassword;

		// encrypt using server's public key
		String cipherText = RSAUtility.encrypt(data, pubKey);

		// sign using client signature private key
		String signature = SignaturesUtility.performSigning(
				userName + "-" + hashedPassword + "-" + encPubKeyStr + "-" + signPubKeyStr,
				rsaSignKeyPair.getPrivate());

		// create request body and then convert it to JSON string
		data = userName + "-" + cipherText + "-" + encPubKeyStr + "-" + signPubKeyStr + "-" + signature;
		LinkedHashMap<String, String> body = new LinkedHashMap<String, String>();
		body.put("data", data);
		final String jsonContent = mapper.writeValueAsString(body);

		// send post request to register new user
		mockMvc.perform(post("/register").contentType(contentType).content(jsonContent)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Registeration")))
				.andExpect(jsonPath("$.message", is("User registered successfully")));

		// check that user was inserted in database
		User user = userDAO.getUser("test123");

		assertEquals("Failure,Wrong user hashed Password fetched", hashedPassword, user.getPassword());
		assertEquals("Failure,Wrong user encryption public key fetched", encPubKeyStr, user.getRsaPubKeyEnc());
		assertEquals("Failure,Wrong user signature public key fetched", signPubKeyStr, user.getRsaPubKeySign());

		// delete user
		userDAO.deleteUser("test123");

	}
}
