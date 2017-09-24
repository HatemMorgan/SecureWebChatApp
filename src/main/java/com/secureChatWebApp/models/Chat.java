package com.secureChatWebApp.models;

import org.springframework.stereotype.Component;

public class Chat {
	String user1;
	String user2;
	/*
	 * encryptedKey is the symmetric key used for encryption of messages between
	 * user1 and user2
	 * 
	 * the symmetric key is encrypted by user1's publickey to make sure that no
	 * one can know this key except user1 (because he is the only one who has
	 * the privatekey)
	 */
	String encryptedKey;

	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
	}

	public String getEncryptedKey() {
		return encryptedKey;
	}

	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}

}
