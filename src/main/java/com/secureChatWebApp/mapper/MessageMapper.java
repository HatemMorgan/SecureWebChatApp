package com.secureChatWebApp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secureChatWebApp.models.Message;

public class MessageMapper implements RowMapper<Message> {

	public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
		Message message = new Message();
		message.setId(rs.getInt("id"));
		message.setSender(rs.getString("sender"));
		message.setReceiver(rs.getString("receiver"));
		message.setText(rs.getString("text"));
		
		boolean isDelivered = rs.getInt("delivered") == 1 ? true : false ;
		message.setDelivered(isDelivered);
		
		boolean isSent = rs.getInt("sent") == 1 ? true : false ;
		message.setSent(isSent);
		
		return message;
	}

}
