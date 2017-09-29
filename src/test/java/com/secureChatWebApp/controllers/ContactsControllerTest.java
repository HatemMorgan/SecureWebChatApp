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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
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
public class ContactsControllerTest {

	private MockMvc mockMvc;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserDAO userDAO;

	private User registeredUser;
	private PrivateKey signPrvKey;
	private List<String> users;

	private PublicKey serverSignPubKey;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		registerNewUser();
		users = addSomeUsers();
	}

	@Test
	public void testGettingContacts() throws Exception {
		String accessToken = generateAccessToken();

		String url = "/contacts/" + registeredUser.getUserName();
		// create request and send it to get contacts
		MvcResult response = mockMvc.perform(get(url).header("x-access-token", accessToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Contacts"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<String> contacts =(List<String>) map.get("contacts");

		// make sure that fetched contacts 2 because limit is set to 2 in the
		// URL.
		assertEquals("Failure,Wrong number of users returned", users.size(), contacts.size());

		for (int i = 0; i < users.size(); i++) {
			assertEquals("Failure,Wrong user returned", users.get(i), contacts.get(i));
		}

		// verify String
		String signature = (String) map.get("signature");
		boolean verfied = SignaturesUtility.performVerification(((List<String>) map.get("contacts")).toString(),
				signature, serverSignPubKey);

		assertEquals("Failure,Verfication signature failed", true, verfied);

	}

	/**
	 * Test getting contacts with limit 2 which means fetching first 2 users
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGettingContactsWithLimits() throws Exception {
		String accessToken = generateAccessToken();

		String url = "/contacts/" + registeredUser.getUserName() + "/2";
		// create request and send it to get contacts
		MvcResult response = mockMvc.perform(get(url).header("x-access-token", accessToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Contacts"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<String> contacts = (List<String>) map.get("contacts");

		// make sure that fetched contacts 2 because limit is set to 2 in the
		// URL.
		assertEquals("Failure,Wrong number of users returned", 2, contacts.size());

		for (int i = 0; i < 2; i++) {
			assertEquals("Failure,Wrong user returned", users.get(i), contacts.get(i));
		}

		// verify String
		String signature = (String) map.get("signature");
		boolean verfied = SignaturesUtility.performVerification(((List<String>) map.get("contacts")).toString(),
				signature, serverSignPubKey);

		assertEquals("Failure,Verfication signature failed", true, verfied);

	}

	/**
	 * Test getting contacts with limit 2 and offset 1 which means skip first
	 * user and fetch next 2 users
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGettingContactsWithLimitAndOffset() throws Exception {
		String accessToken = generateAccessToken();

		String url = "/contacts/" + registeredUser.getUserName() + "/1/2";
		// create request and send it to get contacts
		MvcResult response = mockMvc.perform(get(url).header("x-access-token", accessToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Contacts"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<String> contacts = (List<String>) map.get("contacts");

		// make sure that fetched contacts 2 because limit is set to 2 in the
		// URL.
		assertEquals("Failure,Wrong number of users returned", 2, contacts.size());

		for (int i = 0; i < 2; i++) {
			assertEquals("Failure,Wrong user returned", users.get(i + 1), contacts.get(i));
		}

		// verify String
		String signature = (String) map.get("signature");
		boolean verfied = SignaturesUtility.performVerification(((List<String>) map.get("contacts")).toString(),
				signature, serverSignPubKey);

		assertEquals("Failure,Verfication signature failed", true, verfied);

	}

	@Test
	public void testGettingContactPubKeys() throws Exception{
		String accessToken = generateAccessToken();

		String url = "/contacts/" + users.get(0)+ "/pubKeys";
		// create request and send it to get contacts
		MvcResult response = mockMvc.perform(get(url).header("x-access-token", accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Contacts")))
				.andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> jsonMap = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});
		
		String expectedEncPubKey = "KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==";
		String expectedSignPubKey = "Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==";
		
		assertEquals("Failure,Wrong user's encryption public key returned", expectedEncPubKey,jsonMap.get("encryptionPubKey"));
		assertEquals("Failure,Wrong user's signature public key returned", expectedSignPubKey,jsonMap.get("signaturePubKey"));

		
		System.out.println(jsonMap.get("timeTaken"));
		
	}
	
	@After
	public void tearDown() {
		// delete user after finishing test
		userDAO.deleteUser(registeredUser.getUserName());
		
		for(String userName: users){
			userDAO.deleteUser(userName);
		}
	}

	/**
	 * Create some users and add them to database in order to fetch
	 */
	private List<String> addSomeUsers() {
		List<String> users = new ArrayList<>();
		userDAO.createUser("test", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");
		users.add("test");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");
		users.add("test2");

		userDAO.createUser("test3", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");
		users.add("test3");

		userDAO.createUser("test4", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");
		users.add("test4");

		return users;

	}

	private String generateAccessToken() throws Exception {
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
		return accessToken;
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

		PublicKey encPubKey = RSAUtility.reConstructPublicKey(new BigInteger(pubKeyEnc[0], 16),
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
		String cipherText = RSAUtility.encrypt(data, encPubKey);

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
		String[] serverSignPubKeyStr = map.get("signaturePubKey").split(":");
		serverSignPubKey = RSAUtility.reConstructPublicKey(new BigInteger(serverSignPubKeyStr[0], 16),
				new BigInteger(serverSignPubKeyStr[1], 16));

	}

}
