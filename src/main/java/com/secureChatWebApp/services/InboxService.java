package com.secureChatWebApp.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureChatWebApp.daos.MessageDAO;
import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.exceptions.RequestException;
import com.secureChatWebApp.models.Inbox;
import com.secureChatWebApp.models.Message;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.utilites.SignaturesUtility;

@Service
public class InboxService {

	@Autowired
	MessageDAO messageDAO;
	
	@Autowired
	ServerKeyPairs serverKeyPairs;

	public Map<String, Object>getInbox(String userName) throws Exception {
		try {
			Map<String, Object> map = new LinkedHashMap<>();

			List<Inbox> inbox = messageDAO.getUserInbox(userName);
			map.put("inbox", inbox);

			String strInbox = inbox.toString();
			String signature = SignaturesUtility.performSigning(strInbox,
					serverKeyPairs.getSignatureKeyPair().getPrivate());

			map.put("signature", strInbox + "--" + signature);

			return map;
		} catch (DatabaseException e) {
			throw new RequestException(e.getMessage());
		}
	}
}
