package com.secureChatWebApp.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.secureChatWebApp.exceptions.DatabaseException;

@Component
public class ChatDAO extends JdbcDaoSupport {

	@Autowired
	public ChatDAO(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	/*
	 * encryptedSymmetricKey is the symmetric key used for encryption of
	 * messages between user1 and user2
	 * 
	 * the symmetric key is encrypted by user1's publickey to make sure that no
	 * one can know this key except user1 (because he is the only one who has
	 * the privatekey)
	 */
	public int create(String user1, String user2, String encryptedSymmetricKey) throws DatabaseException {
		String SQL = "insert into chat (user1, user2, encrypted_key) values (?, ?,?)";
		try {
			int inserted = this.getJdbcTemplate().update(SQL, new Object[] { user1, user2, encryptedSymmetricKey });
			return inserted;
		} catch (DuplicateKeyException e) {

			throw new DatabaseException(
					"There is an already messages encryption key between user: " + user1 + " and user: " + user2 + ".");

		} catch (DataIntegrityViolationException ex) {

			throw new DatabaseException(
					"Invalid one or both receiver contact. Please request available contacts to get a the rigth contact's user name before intiating an end to end encryption communication.");

		}

	}

	/*
	 * the symmetric key is encrypted by user1's publickey to make sure that no
	 * one can know this key except user1 (because he is the only one who has
	 * the privatekey)
	 */
	public String getEncryptedChatKey(String user1, String user2) throws DatabaseException {
		String SQL = "select encrypted_key from chat where user1=? and user2=?";

		try {
			String encryptedKey = this.getJdbcTemplate().queryForObject(SQL, new Object[] { user1, user2 },
					String.class);
			return encryptedKey;
		} catch (EmptyResultDataAccessException e) {
			throw new DatabaseException("No key available between user: " + user1 + " and user: " + user2
					+ ". Please intiate a chat connection first using /rest/chat/init api.");
		}
	}

	public int deleteChat(String user1, String user2) {
		String SQL = "DELETE FROM chat WHERE user1=? and user2=?";
		int deleted = this.getJdbcTemplate().update(SQL, new Object[] { user1, user2 });
		return deleted;
	}
}
