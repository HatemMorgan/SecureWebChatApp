package com.secureChatWebApp.controllers;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.secureChatWebApp.services.ContactsService;

@RestController
@RequestMapping("contacts")
public class ContactsController {

	@Autowired
	ContactsService contactsService;

	@RequestMapping(value = { "/{offset}/{limit}","/{limit}","" }, method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, Object>> getContacts(
			@PathVariable(value = "offset") Optional<Integer> optionalOffset,
			@PathVariable(value = "limit") Optional<Integer> optionalLimit, HttpServletRequest request)
			throws Exception {

		int offset;
		int limit;

		offset = optionalOffset.isPresent() ? optionalOffset.get() : -1;
		limit = optionalLimit.isPresent() ? optionalLimit.get() : -1;

		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		boolean isAuthenticated = (boolean) request.getAttribute("isAuthenticated");
		boolean isValidUser = (boolean) request.getAttribute("isValidUser");

		// get startTime added by Security intercepter
		long startTime = (long) request.getAttribute("startTime");

		// get userName from added by Security intercepter from access token
		String userName = (String) request.getAttribute("userName");
		
		// response JSON
		LinkedHashMap<String, Object> responseJSON = new LinkedHashMap<String, Object>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser) {
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "Contacts");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated) {
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "Contacts");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		// if user is authenticated and valid then get contacts and send success
		// response
		double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
		responseJSON.put("domain", "Contacts");
		responseJSON.put("timeTaken", timeTaken + " seconds");
		responseJSON.putAll(contactsService.getContacts(userName, offset, limit));
		return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.OK);

	}

	@RequestMapping(value = "{contactName}/pubKeys", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, Object>> getUserPubKeys(
			@PathVariable(value = "contactName") String contactName, HttpServletRequest request) {
		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		boolean isAuthenticated = (boolean) request.getAttribute("isAuthenticated");
		boolean isValidUser = (boolean) request.getAttribute("isValidUser");

		// get startTime added by Security intercepter
		long startTime = (long) request.getAttribute("startTime");

		// response JSON
		LinkedHashMap<String, Object> responseJSON = new LinkedHashMap<>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser) {
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "Contacts");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated) {
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "Contacts");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		// if user is authenticated and valid then get contact's public keys and
		// send success response
		double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
		responseJSON.put("domain", "Contacts");
		responseJSON.put("timeTaken", timeTaken + " seconds");
		responseJSON.putAll(contactsService.getContactPubKeys(contactName));
		return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.OK);

	}

}
