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
import com.secureChatWebApp.daos.ChatDAO;
import com.secureChatWebApp.daos.MessageDAO;
import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.models.Inbox;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.HashUtility;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class InboxControllerTest {

	private MockMvc mockMvc;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private ChatDAO chatDAO;

	@Autowired
	private MessageDAO messageDAO;

	@Autowired
	private ServerKeyPairs serverKeyPairs;

	private User registeredUser;
	private PrivateKey snederSignPrvKey;
	private List<String> messageText;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		if (registeredUser == null)
			registerNewUser();

		if (messageText == null)
			addMessages();

	}

	@Test
	public void testGetInbox() throws Exception {

		String accessToken = generateAccessToken();

		String url = "/inbox";
		// create request and send it to get contacts
		MvcResult response = mockMvc.perform(get(url).header("x-access-token", accessToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Inbox"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<LinkedHashMap<String, String>> inbox = (List<LinkedHashMap<String, String>>) map.get("inbox");


		assertEquals("Failure,Wrong Inbox fetched", "test3", inbox.get(1).get("sender"));
		assertEquals("Failure,Wrong Inbox fetched", "sdddadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=",
				inbox.get(1).get("message"));
		
		assertEquals("Failure,Wrong Inbox fetched", "test2", inbox.get(0).get("sender"));
		assertEquals("Failure,Wrong Inbox fetched", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=",
				inbox.get(0).get("message"));

		
		

	}
	
	@After
	public void tearDown() {
		messageDAO.deleteMessage("test1", "test2");
		messageDAO.deleteMessage("test1", "test3");
		
		userDAO.deleteUser("test1");
		userDAO.deleteUser("test2");
		userDAO.deleteUser("test3");


	}

	private String generateAccessToken() throws Exception {
		// creating another calendar in UTC timeZone and set it to current time
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date());
		Long epochUTCDate = cal.getTimeInMillis();

		// signature = sign(signPrvKey,{HashedPassword}:{date})
		String signature = SignaturesUtility.performSigning(registeredUser.getPassword() + ":" + epochUTCDate,
				snederSignPrvKey);

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
		String userName = "test1";
		String hashedPassword = HashUtility.hashSHA1("1234");

		RSAPublicKey rsaEncPubKey = ((RSAPublicKey) RSAUtility.generatetKeyPair().getPublic());
		// key parameters must be sent in hexadecimal format
		String encPubKeyStr = String.format("%040x", rsaEncPubKey.getModulus()) + ":"
				+ String.format("%040x", rsaEncPubKey.getPublicExponent());

		KeyPair rsaSignKeyPair = RSAUtility.generatetKeyPair();
		RSAPublicKey rsaSignPubKey = ((RSAPublicKey) rsaSignKeyPair.getPublic());
		// key parameters must be sent in hexadecimal format
		String signPubKeyStr = String.format("%040x", rsaSignPubKey.getModulus()) + ":"
				+ String.format("%040x", rsaSignPubKey.getPublicExponent());

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
		String jsonContent = mapper.writeValueAsString(body);

		// send post request to register new user
		mockMvc.perform(post("/register").contentType(contentType).content(jsonContent)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Registeration")))
				.andExpect(jsonPath("$.message", is("User registered successfully")));

		registeredUser = userDAO.getUser("test1");
		snederSignPrvKey = rsaSignKeyPair.getPrivate();

	}

	private void addMessages() throws InterruptedException, DatabaseException {

		messageText = new ArrayList<String>();

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test3", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

		messageDAO.createMessage("test3", "test1", "oiddsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		messageDAO.createMessage("test3", "test1", "sdddadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		

	}

}
