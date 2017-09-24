package com.secureChatWebApp.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

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
	public int create(String user1, String user2, String encryptedSymmetricKey) {
		String SQL = "insert into chat (user1, user2, encrypted_key) values (?, ?,?)";
		try {
			int inserted = this.getJdbcTemplate().update(SQL, new Object[] { user1, user2, encryptedSymmetricKey });
			return inserted;
		} catch (DuplicateKeyException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	/*
	 * the symmetric key is encrypted by user1's publickey to make sure that no
	 * one can know this key except user1 (because he is the only one who has
	 * the privatekey)
	 */
	public String getEncryptedChatKey(String user1, String user2) {
		String SQL = "select encrypted_key from chat where user1=? and user2=?";
		String encryptedKey = this.getJdbcTemplate().queryForObject(SQL, new Object[] { user1, user2 }, String.class);
		return encryptedKey;
	}
	
	public int deleteChat(String user1, String user2) {
		String SQL = "DELETE FROM chat WHERE user1=? and user2=?";
		int deleted = this.getJdbcTemplate().update(SQL, new Object[] { user1,user2 });
		return deleted;
	}
}
