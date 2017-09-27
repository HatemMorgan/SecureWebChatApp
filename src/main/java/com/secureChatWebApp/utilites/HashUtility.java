package com.secureChatWebApp.utilites;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class HashUtility {

	public static String hashSHA256(String plainText) throws NoSuchAlgorithmException {
		/*
		 * MessageDigest java Docs :
		 * 
		 * https://docs.oracle.com/javase/8/docs/api/java
		 * /security/MessageDigest.html
		 */
		MessageDigest md = MessageDigest.getInstance("SHA-256");

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
		System.out.println(hashSHA256("Hatem1995"));
		System.out.println(new String("b34745c91557ddf3db99f3d283f81d2bf534643ef2e889c4e2ec97380fe3c77b")
				.equals(hashSHA256("Hatem1995").toLowerCase()));
	}

}
