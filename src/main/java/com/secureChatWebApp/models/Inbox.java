package com.secureChatWebApp.models;

public class Inbox {

	private String sender;
	private String message;
	private String timestamp;
	private String encryptedChatKey;
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getEncryptedChatKey() {
		return encryptedChatKey;
	}

	public void setEncryptedChatKey(String encryptedChatKey) {
		this.encryptedChatKey = encryptedChatKey;
	}

	@Override
	public String toString() {
		return "Inbox [sender=" + sender + ", message=" + message + ", timestamp=" + timestamp + ", encryptedChatKey="
				+ encryptedChatKey + "]";
	}

	

}
