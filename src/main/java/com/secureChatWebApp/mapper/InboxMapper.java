package com.secureChatWebApp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secureChatWebApp.models.Inbox;

public class InboxMapper implements RowMapper<Inbox> {

	@Override
	public Inbox mapRow(ResultSet rs, int rowNum) throws SQLException {
		Inbox inbox = new Inbox();
		inbox.setSender(rs.getString("sender"));
		inbox.setMessage(rs.getString("text"));
		inbox.setTimestamp(rs.getString("timeStamp"));
		return inbox;
	}

}
