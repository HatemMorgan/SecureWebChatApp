package com.secureChatWebApp.controllers;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> login(HttpServletRequest request) {

		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		boolean isAuthenticated = (boolean) request.getAttribute("isAuthenticated");
		boolean isValidUser = (boolean) request.getAttribute("isValidUser");

		// get startTime added by Security intercepter
		long startTime = (long) request.getAttribute("startTime");

		// response JSON
		LinkedHashMap<String, String> responseJSON = new LinkedHashMap<String, String>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser) {
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "Login");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated) {
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "Login");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		// if user is authenticated and valid then send a success
		// response Json
		double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
		responseJSON.put("domain", "Login");
		responseJSON.put("message", "User logged in successfully");
		responseJSON.put("timeTaken", timeTaken + " seconds");
		return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.OK);

	}
}
