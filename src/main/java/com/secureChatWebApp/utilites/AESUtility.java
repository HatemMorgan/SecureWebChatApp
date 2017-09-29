package com.secureChatWebApp.utilites;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

public class AESUtility {

	/**
	 * default block size is 16 byte so the generated IV is also 16 bytes
	 */
	public static byte[] encrypt(String plaintext, SecretKey key)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] iv = cipher.getIV();
		byte[] byteCipherText = cipher.doFinal(plaintext.getBytes());

		// concatenating IV to byteCipherText to form one cipher text
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(byteCipherText);

		byte[] finalData = outputStream.toByteArray();

		return finalData;
	}

	public static String decrypt(byte[] byteCipherText, SecretKey key)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		// get IV and cipher text from byteCipherText resulted after
		// concatenation of encrypted plain text and IV
		byte[] iv = Arrays.copyOfRange(byteCipherText, 0, 16);
		byte[] cipherText = Arrays.copyOfRange(byteCipherText, 16, byteCipherText.length);

		IvParameterSpec iv_specs = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, iv_specs);

		byte[] plainTextBytes = cipher.doFinal(cipherText);
		String plainText = new String(plainTextBytes);
		return plainText;
	}

	public static SecretKey generatetSecretEncryptionKey() throws Exception {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128); // The AES key size in number of bits
		SecretKey secKey = generator.generateKey();
		return secKey;
	}

	/**
	 * Convert a binary byte array into readable hex form
	 * 
	 * @param hash
	 * @return
	 */
	public static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}
}
