package com.secureChatWebApp.daos;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.mapper.InboxMapper;
import com.secureChatWebApp.mapper.MessageMapper;
import com.secureChatWebApp.models.Inbox;
import com.secureChatWebApp.models.Message;

@Component
public class MessageDAO extends JdbcDaoSupport {

	@Autowired
	public MessageDAO(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	public String getCurrentFromatedUTCDate() {
		// creating another calendar in UTC timeZone and set it to current time
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date());
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		simpleDateFormat.setTimeZone(timeZone);
		String formatedDate = simpleDateFormat.format(cal.getTime());
		return formatedDate;
	}

	public List<Inbox> getUserInbox(String userName) throws DatabaseException {
		String SQL = "SELECT m.sender,m.text,m.timeStamp,c.encrypted_key AS encryptedChatKey " 
						+ "FROM messages m INNER JOIN chat c "
						+"on m.sender=c.user2 and m.receiver = c.user1 "
						+ "Where m.receiver = ? AND m.id in " 
							+ "(SELECT MAX(m2.id) "
							+ "FROM messages m2 " 
							+ "WHERE m2.receiver = ? and m2.delivered = 0 " 
							+ "GROUP BY m2.sender) "
							+ "ORDER BY m.id DESC";
		try {
			List<Inbox> inbox = this.getJdbcTemplate().query(SQL, new Object[] {userName, userName }, new InboxMapper());
			return inbox;
		} catch (EmptyResultDataAccessException ex) {
			throw new DatabaseException("No messages added yet");
		}
	}

	public Message createMessage(String sender, String receiver, String text) {
		Message message = new Message();
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setText(text);
		message.setTimestamp(getCurrentFromatedUTCDate());
		message.setSent(true);

		String SQL = "insert into messages(sender,receiver,text,sent,timeStamp) " + "values(?,?,?,1,?)";

		int inserted = this.getJdbcTemplate().update(SQL,
				new Object[] { sender, receiver, text, message.getTimestamp() });

		return inserted == 1 ? message : null;
	}

	public List<Message> dumpMessages() throws DatabaseException {
		String SQL = "select * from messages order by id LIMIT 50";

		try {
			List<Message> messages = this.getJdbcTemplate().query(SQL, new MessageMapper());
			return messages;
		} catch (EmptyResultDataAccessException ex) {
			throw new DatabaseException("No messages added yet");
		}

	}

	public List<Message> getOldMessages(String sender, String receiver, int offset, int limit)
			throws DatabaseException {

		List<Message> messages = null;

		try {
			// default limit 10
			if (limit == -1) {
//				String SQL = "select * from messages where ((sender=? and receiver=?) or (receiver=? and sender=?))"
//						+ "order by id DESC LIMIT 10";
				String SQL = "select * from messages where ((sender=? and receiver=?) or (receiver=? and sender=?))"
						+ "order by id DESC ";
				messages = this.getJdbcTemplate().query(SQL, new Object[] { sender, receiver, sender, receiver },
						new MessageMapper());
			} else {
				if (offset == -1) {
					String SQL = "select * from messages where (sender=? and receiver=?) or (receiver=? and sender=?)"
							+ "order by id DESC LIMIT ?";
					messages = this.getJdbcTemplate().query(SQL,
							new Object[] { sender, receiver, sender, receiver, limit }, new MessageMapper());
				} else {
					String SQL = "select * from messages where (sender=? and receiver=?) or (receiver=? and sender=?)"
							+ "order by id DESC LIMIT ? OFFSET ?";
					messages = this.getJdbcTemplate().query(SQL,
							new Object[] { sender, receiver, sender, receiver, limit, offset }, new MessageMapper());

				}
			}
		} catch (EmptyResultDataAccessException ex) {
			throw new DatabaseException("No messages added yet");
		}
		return messages;

	}

	public int setMessagesDelivered(String senderName, String receiverName) {
		String SQL = "UPDATE messages SET delivered = 1 WHERE sender = ? and receiver = ? and delivered = 0 ";
		int updated = this.getJdbcTemplate().update(SQL, new Object[] { senderName, receiverName });
		return updated;
	}

	public int deleteMessage(String sender, String receiver) {
		String SQL = "DELETE FROM messages WHERE  (sender=? and receiver=?) or (receiver=? and sender=?) ";
		int deleted = this.getJdbcTemplate().update(SQL, new Object[] { sender, receiver, sender, receiver });

		String SQL2 = "ALTER TABLE messages AUTO_INCREMENT=1;";
		this.getJdbcTemplate().update(SQL2);

		return deleted;
	}

}
