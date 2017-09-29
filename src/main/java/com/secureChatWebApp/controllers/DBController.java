package com.secureChatWebApp.controllers;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.services.ChatService;

@RestController
@RequestMapping("dbdump")
public class DBController {

	@Autowired
	ChatService chatService;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<LinkedHashMap<String, Object>> dumpDB() {
		long startTime = System.currentTimeMillis();
		LinkedHashMap<String, Object> responseJSON = new LinkedHashMap<>();
		try {
			
			responseJSON.put("domain", "dbdump");
			responseJSON.put("messages", chatService.dbDump());
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.OK);

		} catch (RequestException e) {
			/*
			 * RequestException(custom exception) thrown if request validation
			 * failed or request body was corrupted
			 */
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "dbdump");
			responseJSON.put("errMessage", e.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.BAD_REQUEST);

		} catch (Exception ex) {

			// other exceptions not handled by service thrown and send
			// INTERNAL_SERVER_ERROR status code (500)
			double timeTaken = ((System.currentTimeMillis() - startTime) / 1000.0);
			responseJSON.put("domain", "dbdump");
			responseJSON.put("errMessage", ex.getMessage());
			responseJSON.put("timeTaken", timeTaken + " seconds");
			return new ResponseEntity<LinkedHashMap<String, Object>>(responseJSON, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
