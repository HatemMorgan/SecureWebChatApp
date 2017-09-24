package com.secureChatWebApp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.secureChatWebApp.models.User;

public class UserMapper implements  RowMapper<User> {

	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			
			user.setUserName(rs.getString("user_name"));
			user.setRsaPubKeyEnc(rs.getString("rsa_pub_key_enc"));
			user.setRsaPubKeySign(rs.getString("rsa_pub_key_sign"));
			
			return user;
	}

}
