package com.secureChatWebApp.services;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.models.ServerKeyPairs;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.SignaturesUtility;

@Service
public class ContactsService {

	@Autowired
	UserDAO userDAO;

	@Autowired
	ServerKeyPairs serverKeyPairs;

	/**
	 * getContacts will get contacts and will perform signing on all contacts
	 * returned using server's private key to defend any corruption to data and
	 * maintain data integrity so the user can make sure that contacts are
	 * coming from server and are not corrupted by any attacker along the pass
	 * to the client from server
	 * 
	 * @param offset
	 *            index to start getting contacts from
	 * @param limit
	 *            number of contacts to load
	 * @return LinkedHashMap<String, Object> which consists of two key values
	 *         contacts and signature
	 * @throws Exception
	 */
	public LinkedHashMap<String, Object> getContacts(String userName, int offset, int limit) throws Exception {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

		List<String> contacts = userDAO.getUsers(userName, offset, limit);
		map.put("contacts", contacts);

		// mapper used to change JSON from java object to string and vice versa
		// ObjectMapper mapper = new ObjectMapper();
		//
		// // convert list to string
		// String contactsStr = mapper.writeValueAsString(contacts);
		// map.put("contacts", contactsStr);

		String signature = SignaturesUtility.performSigning(contacts.toString(),
				serverKeyPairs.getSignatureKeyPair().getPrivate());

		map.put("signature", signature);
		return map;
	}

	/**
	 * get Contact's public keys for signatures and encryption
	 * 
	 * @param contactName
	 * @return
	 */
	public LinkedHashMap<String, String> getContactPubKeys(String contactName) {
		User contact = userDAO.getUser(contactName);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("encryptionPubKey", contact.getRsaPubKeyEnc());
		map.put("signaturePubKey", contact.getRsaPubKeySign());
		return map;
	}
}
