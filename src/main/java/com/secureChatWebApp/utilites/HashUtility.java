package com.secureChatWebApp.utilites;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class HashUtility {

	public static String hashSHA1(String plainText) throws NoSuchAlgorithmException {
		/*
		 * MessageDigest java Docs :
		 * 
		 * https://docs.oracle.com/javase/8/docs/api/java
		 * /security/MessageDigest.html
		 */
		MessageDigest md = MessageDigest.getInstance("SHA1");

		/*
		 * The data is processed through it using the update method
		 */
		md.update(plainText.getBytes());

		/*
		 * The digest method can be called once for a given number of updates.
		 * After digest has been called, the MessageDigest object is reset to
		 * its initialized state.
		 */
		byte byteData[] = md.digest();

		return DatatypeConverter.printHexBinary(byteData).toLowerCase();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(hashSHA1("1234"));
		System.out.println(new String("7110eda4d09e062aa5e4a390b0a572ac0d2c0220")
				.equals(hashSHA1("1234").toLowerCase()));
	}

}
