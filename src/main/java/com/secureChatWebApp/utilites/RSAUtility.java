package com.secureChatWebApp.utilites;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAUtility {

	/**
	 * Asymmetric RSA encryption
	 * 
	 * @param plainText
	 *            Text to be encrypted
	 * @param pubKey
	 *            public key that will be used in RSA encryption
	 * @return return cipher text
	 * 
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public static String encrypt(String plainText, PublicKey pubKey) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");

		System.out.println(cipher.getProvider() + "  " + cipher.getAlgorithm() + "  " + cipher.getBlockSize() + "  "
				+ cipher.getParameters());

		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return new String(Base64.getEncoder().encode(cipherText));
	}

	/**
	 * Asymmetric RSA decryption
	 * 
	 * @param cipherText
	 *            Text to be decrypted
	 * @param privKey
	 *            private key that will be used in RSA decryption
	 * @return return cipher text
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * 
	 */
	public static String decrypt(String cipherText, PrivateKey privKey) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("RSA");

		System.out.println(cipher.getProvider() + "  " + cipher.getAlgorithm() + "  " + cipher.getBlockSize() + "  "
				+ cipher.getParameters());

		cipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] decryptedPlainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		String plainText = new String(decryptedPlainText);
		return plainText;

	}

	/**
	 * KeyPairGenerator class can be used to generate pairs of private and
	 * public keys specific to a certain public-key algorithm.
	 * 
	 * RSA keys must be at least 512 bits long and It can reach a key of 8192
	 * bits but it will take long time up to 3 minutes
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generatetKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(512);
		KeyPair keyPair = kpg.generateKeyPair();
		return keyPair;
	}

	public static PublicKey reConstructPublicKey(String encryptionPubKeyString)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String[] params = encryptionPubKeyString.split(":");
		return reConstructPublicKey(new BigInteger(params[0]), new BigInteger(params[1]));
	}

	/**
	 * Reconstruct RSA public key from its stored parameters
	 * 
	 * @param modulus
	 *            n parameter of private key
	 * @param exponent
	 *            e parameter of private key
	 * @return public key object
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey reConstructPublicKey(BigInteger modulus, BigInteger exponent)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePublic(spec);
	}

	/**
	 * Reconstruct RSA private key from its stored parameters
	 * 
	 * @param modulus
	 *            n parameter of private key
	 * @param exponent
	 *            d parameter of private key
	 * @return private key object
	 * @throws NoSuchAlgorithmException
	 */
	public static PrivateKey reConstructPrivateKey(BigInteger modulus, BigInteger exponent)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, exponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(spec);
	}

}
