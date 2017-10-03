package com.secureChatWebApp.models;

public class Inbox {

	private String sender;
	private String message;
	private String timestamp;

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

	@Override
	public String toString() {
		return "Inbox [sender=" + sender + ", message=" + message + ", timestamp=" + timestamp + "]";
	}

}
