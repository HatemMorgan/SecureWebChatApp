package com.secureChatWebApp.controllers;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.secureChatWebApp.services.PublicKeysService;

@RestController
@RequestMapping("publicKey")
public class PublicKeysController {

	PublicKeysService publicKeysService;

	@Autowired
	public PublicKeysController(PublicKeysService publicKeysService) {
		this.publicKeysService = publicKeysService;
	}

	@RequestMapping(value = "/encryption", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> getEncryptionPublicKey() {
		long startTime = System.currentTimeMillis();

		LinkedHashMap<String, String> json = new LinkedHashMap<>();
		json.put("encryptionPubKey", publicKeysService.getEncryptionPublicKey());

		double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
		json.put("timeTaken", timeTaken + " seconds");

		return new ResponseEntity<LinkedHashMap<String, String>>(json, HttpStatus.OK);
	}

	@RequestMapping(value = "/signature", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> getSignaturePublicKey() {
		long startTime = System.currentTimeMillis();

		LinkedHashMap<String, String> json = new LinkedHashMap<>();
		json.put("signaturePubKey", publicKeysService.getSignaturePublicKey());

		double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
		json.put("timeTaken", timeTaken + " seconds");

		return new ResponseEntity<LinkedHashMap<String, String>>(json, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> getServerPublicKey() {
		long startTime = System.currentTimeMillis();

		LinkedHashMap<String, String> json = new LinkedHashMap<>();
		json.put("encryptionPubKey", publicKeysService.getEncryptionPublicKey());
		json.put("signaturePubKey", publicKeysService.getSignaturePublicKey());

		double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
		json.put("timeTaken", timeTaken + " seconds");

		return new ResponseEntity<LinkedHashMap<String, String>>(json, HttpStatus.OK);
	}
}
