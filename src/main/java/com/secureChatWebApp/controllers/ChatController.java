package com.secureChatWebApp.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.activity.InvalidActivityException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.models.Message;
import com.secureChatWebApp.services.ChatService;

@RestController
@RequestMapping("chat")
public class ChatController {

	@Autowired
	ChatService chatService;

	@RequestMapping(value = "/init", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> initChat(
			@RequestParam(value = "receiverName", required = true) String receiverName,
			@RequestBody LinkedHashMap<String, String> requestBody, HttpServletRequest request) {
		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		Boolean isAuthenticated = (Boolean) request.getAttribute("isAuthenticated");
		Boolean isValidUser = (Boolean) request.getAttribute("isValidUser");
		
		// get startTime.longValue() added by Security intercepter
		Long startTime = (Long) request.getAttribute("startTime");

		// get userName from added by Security intercepter from access token
		String senderName = (String) request.getAttribute("userName");

		// response JSON
		LinkedHashMap<String, String> responseJSON = new LinkedHashMap<String, String>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		try {

			// call to chat service to add new chat key between two users
			chatService.intiateNewChat(senderName, receiverName, requestBody);

			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("message", "Chat intiated successfully");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", e.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);
			
			
		}catch (InvalidActivityException invalidActivityException) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", invalidActivityException.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.FOUND);
		}catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", ex.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/chatKey", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> getEncryptedChatKey(
			@RequestParam(value = "receiverName", required = true) String receiverName, HttpServletRequest request) {

		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		Boolean isAuthenticated = (Boolean) request.getAttribute("isAuthenticated");
		Boolean isValidUser = (Boolean) request.getAttribute("isValidUser");

		// get startTime.longValue() added by Security intercepter
		Long startTime = (Long) request.getAttribute("startTime");

		// get userName from added by Security intercepter from access token
		String senderName = (String) request.getAttribute("userName");

		// response JSON
		LinkedHashMap<String, String> responseJSON = new LinkedHashMap<String, String>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		try {

			// call chat service to get encryptedChatKey:signature
			// where encryptedChatKey = RSAEnc(senderPubKey,key) and it was
			// inserted encrypted after encryption by user
			String data = chatService.getEncryptedChatKey(senderName, receiverName);

			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("data", data);
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", e.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);

		} catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", ex.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, String>> sendMessage(
			@RequestParam(value = "receiverName", required = true) String receiverName,
			@RequestBody LinkedHashMap<String, String> requestBody, HttpServletRequest request) {

		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		Boolean isAuthenticated = (Boolean) request.getAttribute("isAuthenticated");
		Boolean isValidUser = (Boolean) request.getAttribute("isValidUser");

		// get startTime.longValue() added by Security intercepter
		Long startTime = (Long) request.getAttribute("startTime");

		// get userName from added by Security intercepter from access token
		String senderName = (String) request.getAttribute("userName");

		// response JSON
		LinkedHashMap<String, String> responseJSON = new LinkedHashMap<String, String>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		try {

			// call to chat service to add new message to database
			chatService.addMessage(senderName, receiverName, requestBody);

			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("message", "Message sent successfully");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", e.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.BAD_REQUEST);

		} catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", ex.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, String>>(responseJSON, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/getMessages", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, Object>> getOldMessages(
			@RequestParam(value = "receiverName", required = true) String receiverName,
			@RequestParam(value = "offset", required = false) Optional<Integer> optionalOffset,
			@RequestParam(value = "limit", required = false) Optional<Integer> optionalLimit,
			HttpServletRequest request) {

		int offset;
		int limit;

		offset = optionalOffset.isPresent() ? optionalOffset.get() : -1;
		limit = optionalLimit.isPresent() ? optionalLimit.get() : -1;

		/*
		 * load attributes set in the Security intercepter to check if the user
		 * is valid and authenticated
		 */
		Boolean isAuthenticated = (Boolean) request.getAttribute("isAuthenticated");
		Boolean isValidUser = (Boolean) request.getAttribute("isValidUser");

		// get startTime.longValue() added by Security intercepter
		Long startTime = (Long) request.getAttribute("startTime");

		// get userName from added by Security intercepter from access token
		String senderName = (String) request.getAttribute("userName");

		// response JSON
		LinkedHashMap<String, Object> responseJSON = new LinkedHashMap<String, Object>();

		// if user is not valid return error response JSON
		// return a bad request status code (400)
		if (!isValidUser.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "Invalid User. Register as a new User before login.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);
		}

		// if user is not authenticated return error response message
		// with UNAUTHORIZED status code (401)
		if (!isAuthenticated.booleanValue()) {
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", "UnAuthorized user. Could not authenticate user. It may be because "
					+ "request has delay over 4 minutes so please check your internet connection and check that it is secure to avoid reply attacks.");
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.UNAUTHORIZED);
		}

		try {

			// call chat service to get old messages
			List<Message> messages = chatService.getOldMessages(senderName, receiverName, offset, limit);

			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("messages", messages);
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", e.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);

		} catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime.longValue()) / 1000.0);
			responseJSON.put("domain", "Chat");
			responseJSON.put("errMessage", ex.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
