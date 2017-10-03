package com.secureChatWebApp.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.models.Inbox;
import com.secureChatWebApp.services.InboxService;

@RestController
@RequestMapping("inbox")
public class InboxController {
	
	@Autowired
	InboxService inboxService;
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, Object>> getInbox(HttpServletRequest request) {

		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		Boolean isAuthenticated = (Boolean) request.getAttribute("isAuthenticated");
		Boolean isValidUser = (Boolean) request.getAttribute("isValidUser");

		// get startTime.longValue() added by Security intercepter
		Long startTime = (Long) request.getAttribute("startTime");

		// get userName from added by Security intercepter from access token
		String userName = (String) request.getAttribute("userName");

		// response JSON
		LinkedHashMap<String, Object> responseJSON = new LinkedHashMap<String, Object>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Inbox");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Inbox");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		try {

			// call chat service to get old messages
			Map<String, Object> map = inboxService.getInbox(userName);

			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Inbox");
			responseJSON.putAll(map);
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Inbox");
			responseJSON.put("errMessage", e.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);

		} catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Inbox");
			responseJSON.put("errMessage", ex.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
