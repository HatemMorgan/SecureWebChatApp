package com.secureChatWebApp.models;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.secureChatWebApp.utilites.RSAUtility;

@Component
public class ServerKeyPairs {

	private KeyPair encryptionKeyPair;
	private KeyPair signatureKeyPair;

	public ServerKeyPairs() throws NoSuchAlgorithmException {
		System.out.println("Security Configs Intialized");
		init();
	}

	public KeyPair getEncryptionKeyPair() {
		return encryptionKeyPair;
	}

	public KeyPair getSignatureKeyPair() {
		return signatureKeyPair;
	}

	private void init() throws NoSuchAlgorithmException {
		if (wasKeysGenerated()) {
			try {
				readKeys();
				//
			} catch (IOException | InvalidKeySpecException ex) {
				System.out.println(ex.getMessage());
				// IOException or InvalidKeySpecException thrown
				// then generate new keys and write them
				writeKeys();
			}
		} else {
			// first time to generate keys
			writeKeys();
		}
	}

	private boolean writeKeys() throws NoSuchAlgorithmException {

		encryptionKeyPair = RSAUtility.generatetKeyPair();
		signatureKeyPair = RSAUtility.generatetKeyPair();

		// set properties of encryption key pairs
		RSAPublicKey pubKey = (RSAPublicKey) encryptionKeyPair.getPublic();
		RSAPrivateKey prvKey = (RSAPrivateKey) encryptionKeyPair.getPrivate();

		Properties properties = new Properties();
		properties.setProperty("encryption.pub.modulas", pubKey.getModulus().toString());
		properties.setProperty("encryption.pub.exponent", pubKey.getPublicExponent().toString());
		properties.setProperty("encryption.prv.modulas", prvKey.getModulus().toString());
		properties.setProperty("encryption.prv.exponent", prvKey.getPrivateExponent().toString());

		// set properties of signature key pairs
		pubKey = (RSAPublicKey) signatureKeyPair.getPublic();
		prvKey = (RSAPrivateKey) signatureKeyPair.getPrivate();

		properties.setProperty("signature.pub.modulas", pubKey.getModulus().toString());
		properties.setProperty("signature.pub.exponent", pubKey.getPublicExponent().toString());
		properties.setProperty("signature.prv.modulas", prvKey.getModulus().toString());
		properties.setProperty("signature.prv.exponent", prvKey.getPrivateExponent().toString());

		// wirte propeties file
		String userHome = System.getProperty("user.home");
		Path path = Paths.get(userHome, ".serverKeys.properties");
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(path.toString());
			properties.store(fileWriter, null);
		} catch (IOException e) {
			return false;
		} finally {
			if (fileWriter != null)
				try {
					fileWriter.close();
				} catch (IOException e) {
					return false;
				}
		}
		return true;
	}

	/**
	 * Check if the public keys for encryption and signatures were generated
	 * before or it is the first time.
	 * 
	 * By checking the file system for the existing of serverKeys.properties
	 * file in user home directory
	 */
	private boolean wasKeysGenerated() {
		String userHome = System.getProperty("user.home");
		Path path = Paths.get(userHome, ".serverKeys.properties");
		System.out.println(Files.exists(path));
		return Files.exists(path);
	}

	/**
	 * Read saved server keys if they were not loaded
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * 
	 */
	private boolean readKeys() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String userHome = System.getProperty("user.home");
		Path path = Paths.get(userHome, ".serverKeys.properties");

		Properties properties = new Properties();
		try {
			properties.load(new FileReader(path.toString()));

			// getting Encryption Key Pair
			BigInteger pubKeyModulas = new BigInteger(properties.getProperty("encryption.pub.modulas"));
			BigInteger pubKeyExponent = new BigInteger(properties.getProperty("encryption.pub.exponent"));
			BigInteger prvKeyModulas = new BigInteger(properties.getProperty("encryption.prv.modulas"));
			BigInteger prvKeyExponent = new BigInteger(properties.getProperty("encryption.prv.exponent"));

			PrivateKey prvKey = RSAUtility.reConstructPrivateKey(prvKeyModulas, prvKeyExponent);
			PublicKey pubKey = RSAUtility.reConstructPublicKey(pubKeyModulas, pubKeyExponent);
			encryptionKeyPair = new KeyPair(pubKey, prvKey);

			// getting Signature Key Pair
			pubKeyModulas = new BigInteger(properties.getProperty("signature.pub.modulas"));
			pubKeyExponent = new BigInteger(properties.getProperty("signature.pub.exponent"));
			prvKeyModulas = new BigInteger(properties.getProperty("signature.prv.modulas"));
			prvKeyExponent = new BigInteger(properties.getProperty("signature.prv.exponent"));

			prvKey = RSAUtility.reConstructPrivateKey(prvKeyModulas, prvKeyExponent);
			pubKey = RSAUtility.reConstructPublicKey(pubKeyModulas, pubKeyExponent);
			signatureKeyPair = new KeyPair(pubKey, prvKey);

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException ex) {
			throw ex;
		}

		return true;

	}

}
