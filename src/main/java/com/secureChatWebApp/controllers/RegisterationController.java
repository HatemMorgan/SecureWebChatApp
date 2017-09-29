package com.secureChatWebApp.controllers;

import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.services.RegistrationService;

@RestController
@RequestMapping("register")
public class RegisterationController {
	@Autowired
	RegistrationService registrationService;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> register(
			@RequestBody LinkedHashMap<String, String> requestBody) {
		long startTime = System.currentTimeMillis();
		LinkedHashMap<String, String> json = new LinkedHashMap<>();
		try {

			registrationService.register(requestBody.get("data"));

			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			json.put("domain", "Registeration");
			json.put("message", "User registered successfully");
			json.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(json, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			json.put("domain", "Registeration");
			json.put("errMessage", e.getMessage());
			json.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(json, HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			json.put("domain", "Registeration");
			json.put("errMessage", ex.getMessage());
			json.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(json, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
