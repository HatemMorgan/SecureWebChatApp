package com.secureChatWebApp.services;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureChatWebApp.models.ServerKeyPairs;

@Service
public class PublicKeysService {
	private ServerKeyPairs serverKeyPairs;

	@Autowired
	public PublicKeysService(ServerKeyPairs serverKeyPairs) {
		this.serverKeyPairs = serverKeyPairs;
	}

	/**
	 * get encryption public key which is the concatenation of modulus n and
	 * exponent e parameters of public key
	 */
	public String getEncryptionPublicKey() {
		String n = String.format("%040x",
				((RSAPublicKey) serverKeyPairs.getEncryptionKeyPair().getPublic()).getModulus());
		String e = String.format("%040x",
				((RSAPublicKey) serverKeyPairs.getEncryptionKeyPair().getPublic()).getPublicExponent());
		return n + ":" + e;
	}

	/**
	 * get signature public key which is the concatenation of modulus n and
	 * exponent e parameters of public key
	 */
	public String getSignaturePublicKey() {
		String n = String.format("%040x",
				((RSAPublicKey) serverKeyPairs.getSignatureKeyPair().getPublic()).getModulus());
		String e = String.format("%040x",
				((RSAPublicKey) serverKeyPairs.getSignatureKeyPair().getPublic()).getPublicExponent());
		return n + ":" + e;
	}

}
