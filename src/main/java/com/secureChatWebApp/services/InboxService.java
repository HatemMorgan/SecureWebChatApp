package com.secureChatWebApp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureChatWebApp.daos.MessageDAO;
import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.models.Inbox;
import com.secureChatWebApp.models.Message;

@Service
public class InboxService {
	
	@Autowired
	MessageDAO messageDAO;
	
	public List<Inbox> getInbox(String userName) throws RequestException {
		try {
			return messageDAO.getUserInbox(userName);
		} catch (DatabaseException e) {
			throw new RequestException(e.getMessage());
		}
	}
}
