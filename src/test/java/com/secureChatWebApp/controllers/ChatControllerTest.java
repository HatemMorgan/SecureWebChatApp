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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

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
import com.secureChatWebApp.models.Message;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.AESUtility;
import com.secureChatWebApp.utilites.HashUtility;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@WebAppConfiguration
public class ChatControllerTest {

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
	private User receiver;
	private List<String> messageText;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		if (registeredUser == null)
			registerNewUser();
		if (receiver == null)
			receiver = addAnotherUser();
		if (messageText == null)
			addMessages();

	}

	@Test
	public void testInitChat() throws Exception {

		String accessToken = generateAccessToken();

		// generate symmetric key
		String aesKey = AESUtility.bytesToHex(AESUtility.generatetSecretEncryptionKey().getEncoded());

		// encrypt symmetric key with sender's public key for signature
		String senderEncAESKey = RSAUtility.encrypt(aesKey,
				RSAUtility.reConstructPublicKey(registeredUser.getRsaPubKeyEnc()));

		// encrypt symmetric key with receiver's public key for signature
		String receiverEncAESKey = RSAUtility.encrypt(aesKey,
				RSAUtility.reConstructPublicKey(receiver.getRsaPubKeyEnc()));

		String signature = SignaturesUtility.performSigning(senderEncAESKey + ":" + receiverEncAESKey,
				snederSignPrvKey);

		LinkedHashMap<String, String> body = new LinkedHashMap<String, String>();
		body.put("keyEncBySender", senderEncAESKey);
		body.put("keyEncByReceiver", receiverEncAESKey);
		body.put("signature", signature);

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();
		String jsonContent = mapper.writeValueAsString(body);

		MvcResult response = mockMvc
				.perform(post("/chat/init").header("x-access-token", accessToken)
						.param("receiverName", receiver.getUserName()).content(jsonContent).contentType(contentType))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat")))
				.andExpect(jsonPath("$.message", is("Chat intiated successfully"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// convert JSON string to Map
		Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});
		System.out.println("Time Taken = " + map.get("timeTaken"));

	}

	@Test
	public void testGetEncryptedChatKey() throws Exception {
		String accessToken = generateAccessToken();

		// generate symmetric key
		String aesKey = AESUtility.bytesToHex(AESUtility.generatetSecretEncryptionKey().getEncoded());

		// encrypt symmetric key with sender's public key for signature
		String senderEncAESKey = RSAUtility.encrypt(aesKey,
				RSAUtility.reConstructPublicKey(registeredUser.getRsaPubKeyEnc()));

		// encrypt symmetric key with receiver's public key for signature
		String receiverEncAESKey = RSAUtility.encrypt(aesKey,
				RSAUtility.reConstructPublicKey(receiver.getRsaPubKeyEnc()));

		String signature = SignaturesUtility.performSigning(senderEncAESKey + ":" + receiverEncAESKey,
				snederSignPrvKey);

		LinkedHashMap<String, String> body = new LinkedHashMap<String, String>();
		body.put("keyEncBySender", senderEncAESKey);
		body.put("keyEncByReceiver", receiverEncAESKey);
		body.put("signature", signature);

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();
		String jsonContent = mapper.writeValueAsString(body);

		mockMvc.perform(post("/chat/init").header("x-access-token", accessToken)
				.param("receiverName", receiver.getUserName()).content(jsonContent).contentType(contentType))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat")))
				.andExpect(jsonPath("$.message", is("Chat intiated successfully")));

		MvcResult response = mockMvc
				.perform(get("/chat/chatKey").header("x-access-token", accessToken)
						.param("receiverName", receiver.getUserName()).contentType(contentType))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// convert JSON string to Map
		Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
		});

		// dataArr = {encrypedChatKey,Signature}
		String[] dataArr = map.get("data").split(":");

		boolean verfied = SignaturesUtility.performVerification(dataArr[0], dataArr[1],
				serverKeyPairs.getSignatureKeyPair().getPublic());

		assertEquals("Failure, Verfication signature failed", true, verfied);
		assertEquals("Failure, Wrong encrypted chat key retrieved", senderEncAESKey, dataArr[0]);

	}

	@Test
	public void testSendMessage() throws Exception {
		String accessToken = generateAccessToken();

		// generate symmetric key
		SecretKey secKey = AESUtility.generatetSecretEncryptionKey();
		String aesKey = AESUtility.bytesToHex(secKey.getEncoded());

		// encrypt symmetric key with sender's public key for signature
		String senderEncAESKey = RSAUtility.encrypt(aesKey,
				RSAUtility.reConstructPublicKey(registeredUser.getRsaPubKeyEnc()));

		// encrypt symmetric key with receiver's public key for signature
		String receiverEncAESKey = RSAUtility.encrypt(aesKey,
				RSAUtility.reConstructPublicKey(receiver.getRsaPubKeyEnc()));

		String signature = SignaturesUtility.performSigning(senderEncAESKey + ":" + receiverEncAESKey,
				snederSignPrvKey);

		LinkedHashMap<String, String> body = new LinkedHashMap<String, String>();
		body.put("keyEncBySender", senderEncAESKey);
		body.put("keyEncByReceiver", receiverEncAESKey);
		body.put("signature", signature);

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();
		String jsonContent = mapper.writeValueAsString(body);

		mockMvc.perform(post("/chat/init").header("x-access-token", accessToken)
				.param("receiverName", receiver.getUserName()).content(jsonContent).contentType(contentType))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat")))
				.andExpect(jsonPath("$.message", is("Chat intiated successfully")));

		String message = "testing";
		String encryptedMessage = AESUtility.bytesToHex(AESUtility.encrypt(message, secKey));

		signature = SignaturesUtility.performSigning(
				registeredUser.getUserName() + ":" + receiver.getUserName() + ":" + encryptedMessage, snederSignPrvKey);

		body = new LinkedHashMap<String, String>();
		body.put("encMessage", encryptedMessage);
		body.put("signature", signature);

		jsonContent = mapper.writeValueAsString(body);

		mockMvc.perform(post("/chat/sendMessage").header("x-access-token", accessToken)
				.param("receiverName", receiver.getUserName()).content(jsonContent).contentType(contentType))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat")))
				.andExpect(jsonPath("$.message", is("Message sent successfully")));

		// delete message after finshing test
		messageDAO.deleteMessage(registeredUser.getUserName(), receiver.getUserName());
	}

	@Test
	public void testGettingOldMessages() throws Exception {

		String accessToken = generateAccessToken();

		String url = "/chat/getMessages";
		// create request and send it to get contacts
		MvcResult response = mockMvc
				.perform(get(url).param("receiverName", receiver.getUserName()).header("x-access-token", accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<LinkedHashMap<String, String>> messages = (List<LinkedHashMap<String, String>>) map.get("messages");

		// make sure that fetched contacts 2 because limit is set to 2 in the
		// URL.
		assertEquals("Failure,Wrong number of users returned", messageText.size(), messages.size());

		for (int i = 0; i < messageText.size(); i++) {
			assertEquals("Failure,Wrong message returned", messageText.get(5 - i), messages.get(i).get("text"));
		}

	}

	/**
	 * Test getting contacts with limit 2 which means fetching first 2 users
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGettingContactsWithLimits() throws Exception {

		String accessToken = generateAccessToken();

		String url = "/chat/getMessages";
		// create request and send it to get contacts
		MvcResult response = mockMvc
				.perform(get(url).param("receiverName", receiver.getUserName()).param("limit", "2")
						.header("x-access-token", accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<LinkedHashMap<String, String>> messages = (List<LinkedHashMap<String, String>>) map.get("messages");

		// make sure that fetched contacts 2 because limit is set to 2 in the
		// URL.
		assertEquals("Failure,Wrong number of users returned", 2, messages.size());

		for (int i = 0; i < 2; i++) {
			assertEquals("Failure,Wrong user returned", messageText.get(5 - i), messages.get(i).get("text"));
		}

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

		String url = "/chat/getMessages";
		// create request and send it to get contacts
		MvcResult response = mockMvc
				.perform(get(url).param("receiverName", receiver.getUserName()).param("limit", "2").param("offset", "1")
						.header("x-access-token", accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.domain", is("Chat"))).andReturn();

		String json = response.getResponse().getContentAsString();

		// mapper used to change JSON from java object to string and vice versa
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to Map
		Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
		});

		List<LinkedHashMap<String, String>> messages = (List<LinkedHashMap<String, String>>) map.get("messages");

		// make sure that fetched contacts 2 because limit is set to 2 in the
		// URL.
		assertEquals("Failure,Wrong number of users returned", 2, messages.size());

		for (int i = 0; i < 2; i++) {
			assertEquals("Failure,Wrong user returned", messageText.get(5 - i - 1), messages.get(i).get("text"));
		}

	}

	@After
	public void tearDown() {
		// delete chat
		chatDAO.deleteChat(registeredUser.getUserName(), receiver.getUserName());
		chatDAO.deleteChat(receiver.getUserName(), registeredUser.getUserName());

		// delete messages
		messageDAO.deleteMessage("test123", "test2");

		// delete users after finishing test
		userDAO.deleteUser(registeredUser.getUserName());
		userDAO.deleteUser(receiver.getUserName());

	}

	/**
	 * Create another user and add him to database in order to be fetched
	 */
	private User addAnotherUser() {
		User user = new User();
		user.setUserName("test2");
		user.setPassword("2123ejdq124fa32");
		user.setRsaPubKeyEnc(
				"9579395940609983593307985278396088956051575943622517115758142329514659049231799125540545342549638974014020899333615940728093556477765550635369547517163601:3");
		user.setRsaPubKeySign("");

		userDAO.createUser(user.getUserName(), user.getPassword(), user.getRsaPubKeyEnc(), user.getRsaPubKeySign());

		return user;
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
		String jsonContent = mapper.writeValueAsString(body);

		// send post request to register new user
		mockMvc.perform(post("/register").contentType(contentType).content(jsonContent)).andExpect(status().isOk())
				.andExpect(jsonPath("$.domain", is("Registeration")))
				.andExpect(jsonPath("$.message", is("User registered successfully")));

		registeredUser = userDAO.getUser("test123");
		snederSignPrvKey = rsaSignKeyPair.getPrivate();

	}

	private void addMessages() throws InterruptedException {

		messageText = new ArrayList<String>();

		messageDAO.createMessage("test123", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageText.add("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test123", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageText.add("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test123", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageText.add("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test123", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");
		messageText.add("PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test123", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		messageText.add("PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test123", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		messageText.add("asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

	}

}
