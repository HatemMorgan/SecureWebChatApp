package com.secureChatWebApp.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@Service
public class RegistrationService {
	@Autowired
	UserDAO userDAO;
	@Autowired
	ServerKeyPairs serverKeyPairs;

	public boolean register(String requestBody)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, SignatureException, InvalidKeySpecException, RequestException {

		// get cipher text and signature
		String[] body = requestBody.split("-");

		if (body.length != 5)
			throw new RequestException(
					"Invalid request body. request body JSON must have a data key with value= RSAEnc({userName}:{hashPassword}:{encPubKey}:{signPubKey}):RSASign({userName}:{hashPassword}:{encPubKey}:{signPubKey})");

		String username = body[0];
		String cipherText = body[1];
		String encPubKey = body[2];
		String signPubKey = body[3];
		String signature = body[4];

		// decrypt to get plainText which is:
		// {hashPassword}
		try {
			String hashedPassword = RSAUtility.decrypt(cipherText, serverKeyPairs.getEncryptionKeyPair().getPrivate());

			String s[] = { username, hashedPassword, encPubKey, signPubKey };

			String errMessage = validate(s);
			if (errMessage != null) {
				throw new RequestException(errMessage);
			} else {
				String plainText = username + "-" + hashedPassword + "-" + encPubKey + "-" + signPubKey;
				// check that signature is valid to check integrity of the
				// request
				// and make sure that is was no corrupted
				boolean verify = SignaturesUtility.performVerification(plainText, signature,
						RSAUtility.reConstructPublicKey(signPubKey));

				if (!verify) {
					throw new RequestException(
							"Request Body was corrupted so please check your internet connection and try again");

				} else {

					return registerNewUser(s);
				}
			}

		} catch (BadPaddingException e) {
			throw new RequestException(
					"InValid Encrypted message. {userName}:{hashPassword}:{encPubKey}:{signPubKey} must be encrypted by server's public key");
		}

	}

	/**
	 * call userDAO to create a new user and insert it into database
	 * 
	 * @param s
	 *            {userName}:{hashPassword}:{encPubKey}:{signPubKey}
	 * @return
	 */
	private boolean registerNewUser(String[] s) {

		int inserted = userDAO.createUser(s[0], s[1], s[2], s[3]);
		return inserted == 1 ? true : false;
	}

	/**
	 * check that decrypted body contains 4 elements
	 * {userName,hashPass,encPubKey,signPubKey}
	 * 
	 * 1- checks that userName is of length between 5 and 12 inclusive
	 * 
	 * 2- password is of length 64 to make sure that it was hashed by SHA256
	 * 
	 * 3- encPubKey and signPubKey are not nulls
	 * 
	 * @param s
	 *            {userName}:{hashPassword}:{encPubKey}:{signPubKey}
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String validate(String[] s) throws NoSuchAlgorithmException {
		if (s.length != 4)
			return "To register as a new user, userName, hashed password, public key for RSA encryption and public key for signatures must be provided and seperated by :";

		if (!(s[0].length() >= 5 && s[0].length() <= 12))
			return "UserName must be between 5 and 12 characters.";

		if (s[1].length() != 40)
			return "Password must be hashed by SHA256.";

		if (s[2] == null || !RSAUtility.checkIfPublicKeyValid(s[2]))
			return "Public key for encryption must not be null or invalid. It must be a valid public key. eg moduls(n):exponent(e)";

		if (s[3] == null || !RSAUtility.checkIfPublicKeyValid(s[3]))
			return "Public key for signaturing must not be null or invalid. It must be a valid public key. eg moduls(n):exponent(e)";

		return null;
	}

}
