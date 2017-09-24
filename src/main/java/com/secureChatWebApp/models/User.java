package com.secureChatWebApp.models;

import org.springframework.stereotype.Component;

@Component
public class User {
	String userName;
	String password;
	String rsaPubKeyEnc;
	String rsaPubKeySign;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRsaPubKeyEnc() {
		return rsaPubKeyEnc;
	}
	public void setRsaPubKeyEnc(String rsaPubKeyEnc) {
		this.rsaPubKeyEnc = rsaPubKeyEnc;
	}
	public String getRsaPubKeySign() {
		return rsaPubKeySign;
	}
	public void setRsaPubKeySign(String rsaPubKeySign) {
		this.rsaPubKeySign = rsaPubKeySign;
	}
	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", rsaPubKeyEnc=" + rsaPubKeyEnc
				+ ", rsaPubKeySign=" + rsaPubKeySign + "]";
	}
	
	
	
}	
