package com.secureChatWebApp.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureChatWebApp.daos.ChatDAO;
import com.secureChatWebApp.daos.MessageDAO;
import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.models.Message;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@Service
public class ChatService {

	@Autowired
	ChatDAO chatDAO;

	@Autowired
	UserDAO userDAO;

	@Autowired
	MessageDAO messageDAO;

	@Autowired
	ServerKeyPairs serverKeyPairs;

	/**
	 * Insert a new Chat key between two users
	 * 
	 * Sender will create a symmetric key to encrypt communication to receiver
	 * 
	 * Sender will send key encrypted with its public key concatenated with the
	 * encryption of key with receiver public key.
	 * 
	 * The key is encrypted with sender's and receiver public keys in order to
	 * allow them only to know the key
	 * 
	 * The server will store both encrypted key
	 * 
	 * @param senderName
	 *            Sender userName
	 * @param receiverName
	 *            Receiver userName
	 * @param body
	 *            strKey will consist of {RSAEnc(senderPubKey,key)}:
	 *            {RSAEnc(senderPubKey,key)}:signature
	 * 
	 * @return
	 * @throws RequestException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidActivityException 
	 */
	public void intiateNewChat(String senderName, String receiverName, LinkedHashMap<String, String> body)
			throws RequestException, InvalidKeyException, NoSuchAlgorithmException, SignatureException,
			InvalidKeySpecException, InvalidActivityException {

		// get body message components
		String keyEncBySender = body.get("keyEncBySender");
		String keyEncByReceiver = body.get("keyEncByReceiver");
		String signature = body.get("signature");

		try {

			// get sender public key for signature verification
			User sender = userDAO.getUser(senderName);
			String message = keyEncBySender + ":" + keyEncByReceiver;
			String senderSignPubKey = sender.getRsaPubKeySign();

			// verify signature
			boolean verified = SignaturesUtility.performVerification(message, signature,
					RSAUtility.reConstructPublicKey(senderSignPubKey));

			if (!verified)
				throw new RequestException(
						"Request Body was corrupted so please check your internet connection and try again");

			// insert both keys
			int inserted1 = chatDAO.create(senderName, receiverName, keyEncBySender);
			int inserted2 = chatDAO.create(receiverName, senderName, keyEncByReceiver);

			// chat was not inserted into database
			if (inserted1 == 0 || inserted2 == 0)
				throw new RequestException("Cannot insert new chat. Please report this problem and try again");

		} catch (DatabaseException e) {
			throw new RequestException(e.getMessage());
		}
	}

	/**
	 * Get encrypted chat key between sender and receiver users
	 * 
	 * @param senderName
	 *            sender userName
	 * @param receiverName
	 *            receiver userName
	 * @return encryptedChatKey:signature where
	 * 
	 *         encryptedChatKey = RSAEnc(senderPubKey,key) signature =
	 *         RSASign(serverPrvKey,encryptedChatKey)
	 * @throws Exception
	 */
	public String getEncryptedChatKey(String senderName, String receiverName) throws Exception {

		try {
			String encryptedChatKey = chatDAO.getEncryptedChatKey(senderName, receiverName);

			String signature = SignaturesUtility.performSigning(encryptedChatKey,
					serverKeyPairs.getSignatureKeyPair().getPrivate());

			return encryptedChatKey + ":" + signature;
		} catch (DatabaseException e) {
			throw new RequestException(e.getMessage());
		}

	}

	public void addMessage(String senderName, String receiverName, LinkedHashMap<String, String> body)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException,
			RequestException {
		// get body components
		String encryptedMessage = body.get("encMessage");
		String signature = body.get("signature");

		// verify signature using sender's public key for signatures
		User sender = userDAO.getUser(senderName);
		boolean verified = SignaturesUtility.performVerification(
				senderName + ":" + receiverName + ":" + encryptedMessage, signature,
				RSAUtility.reConstructPublicKey(sender.getRsaPubKeySign()));

		if (!verified)
			throw new RequestException(
					"Request Body was corrupted so please check your internet connection and try again");

		int inserted = messageDAO.createMessage(senderName, receiverName, encryptedMessage);
		if (inserted == 0)
			throw new RequestException("Cannot insert new message. Please report this problem and try again");
	}

	public List<Message> dbDump() throws RequestException {
		try {
			return messageDAO.dumpMessages();
		} catch (DatabaseException e) {
			throw new RequestException(e.getMessage());
		}
	}

	public List<Message> getOldMessages(String senderName, String receiverName, int offset, int limit) throws RequestException {
		try {
			return messageDAO.getOldMessages(senderName, receiverName, offset, limit);
		} catch (DatabaseException e) {
			throw new RequestException(e.getMessage());
		}
	}
}
