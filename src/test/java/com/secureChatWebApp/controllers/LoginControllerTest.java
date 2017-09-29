package com.secureChatWebApp.controllers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

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
import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.HashUtility;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class LoginControllerTest {

	private MockMvc mockMvc;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserDAO userDAO;

	private User registeredUser;
	private PrivateKey signPrvKey;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		registerNewUser();
	}

	@Test
	public void testLogging() throws Exception {

		// creating another calendar in UTC timeZone and set it to current time
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date());
		Long epochUTCDate = cal.getTimeInMillis();

		// signature = sign(signPrvKey,{HashedPassword}:{date})
		String signature = SignaturesUtility.performSigning(registeredUser.getPassword() + ":" + epochUTCDate,
				signPrvKey);

		// create x-access-token
		// x-access-token = {userName}:{date}:signature
		String accessToken = registeredUser.getUserName() + ":" + epochUTCDate + ":" + signature;

		MvcResult response = mockMvc.perform(get("/login").header("x-access-token", accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Login")))
				.andExpect(jsonPath("$.message", is("User logged in successfully"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});

		System.out.println("Time taken = " + map.get("timeTaken"));
	}

	/**
	 * Testing delaying request by more than 4 minutes to test that request will
	 * not be authenticated and an error message will be returned in response
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDelayingRequestDate() throws Exception {
		// creating another calendar in UTC timeZone and set it to current time
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date());

		/*
		 * minus 5 minutes from current date time to make request as if it was
		 * sent from 5 minutes
		 */
		cal.add(Calendar.MINUTE, -5);
		Long epochUTCDate = cal.getTimeInMillis();

		// signature = sign(signPrvKey,{HashedPassword}:{date})
		String signature = SignaturesUtility.performSigning(registeredUser.getPassword() + ":" + epochUTCDate,
				signPrvKey);

		// create x-access-token
		// x-access-token = {userName}:{date}:signature
		String accessToken = registeredUser.getUserName() + ":" + epochUTCDate + ":" + signature;

		String expectedErrMessage = "UnAuthorized user. Could not authenticate user. It may be because "
				+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.";

		MvcResult response = mockMvc.perform(get("/login").header("x-access-token", accessToken))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.domain", is("Login")))
				.andExpect(jsonPath("$.errMessage", is(expectedErrMessage))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});

		System.out.println("Time taken = " + map.get("timeTaken"));
	}

	@After
	public void tearDown() {
		// delete user after finishing test
		userDAO.deleteUser(registeredUser.getUserName());
	}

	/**
	 * Register with a new user and initialize registeredUser attribute to new
	 * user added and signPrvKey attribute to private key for signature of this
	 * user
	 * 
	 * @throws Exception
	 */
	private void registerNewUser() throws Exception {
		// request public key of server
		MvcResult response = mockMvc.perform(get("/publicKey")).andExpect(status().isOk()).andReturn();
		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});
		// System.out.println(map);
		String[] pubKeyEnc = map.get("encryptionPubKey").split(":");

		PublicKey pubKey = RSAUtility.reConstructPublicKey(new BigInteger(pubKeyEnc[0], 16),
				new BigInteger(pubKeyEnc[1], 16));

		// create a new client
		String userName = "test123";
		String hashedPassword = HashUtility.hashSHA1("1234");

		RSAPublicKey rsaEncPubKey = ((RSAPublicKey) RSAUtility.generatetKeyPair().getPublic());
		String encPubKeyStr = rsaEncPubKey.getModulus() + ":" + rsaEncPubKey.getPublicExponent();

		KeyPair rsaSignKeyPair = RSAUtility.generatetKeyPair();
		RSAPublicKey rsaSignPubKey = ((RSAPublicKey) rsaSignKeyPair.getPublic());
		String signPubKeyStr = rsaSignPubKey.getModulus() + ":" + rsaSignPubKey.getPublicExponent();

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
		registeredUser = userDAO.getUser("test123");
		signPrvKey = rsaSignKeyPair.getPrivate();

	}
}
