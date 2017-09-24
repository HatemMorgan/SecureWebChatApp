package com.secureChatWebApp.daos;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.secureChatWebApp.mapper.MessageMapper;
import com.secureChatWebApp.models.Message;

public class MessageDAO extends JdbcDaoSupport {

	@Autowired
	public MessageDAO(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	public int createMessage(String sender, String receiver, String text) {
		String SQL = "insert into messages(sender,receiver,text,sent) "
				+ "values(?,?,?,1)";
		try {
			int inserted = this.getJdbcTemplate().update(SQL, new Object[] { sender, receiver, text });
			return inserted;
		} catch (DuplicateKeyException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	public List<Message> dumpMessages() {
		String SQL = "select * from messages order by id LIMIT 50";
		List<Message> messages = this.getJdbcTemplate().query(SQL, new MessageMapper());

		return messages;
	}

	public List<Message> getOldMessages(String sender, String receiver, int offset, int limit) {
		String SQL = "select * from messages where (sender=? and receiver=?) or (receiver=? and sender=?)"
				+ "order by id LIMIT ? OFFSET ?";
		List<Message> messages = this.getJdbcTemplate().query(SQL,
				new Object[] { sender, receiver, sender, receiver, limit,offset  }, new MessageMapper());

		return messages;
	}

	public int deleteMessage(String sender, String receiver) {
		String SQL = "DELETE FROM messages WHERE  (sender=? and receiver=?) or (receiver=? and sender=?) ";
		int deleted = this.getJdbcTemplate().update(SQL, new Object[] { sender, receiver,sender,receiver});
		
		String SQL2 = "ALTER TABLE messages AUTO_INCREMENT=1;";
		this.getJdbcTemplate().update(SQL2);
	
		return deleted;
	}

}
