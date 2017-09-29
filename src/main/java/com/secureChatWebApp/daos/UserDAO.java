package com.secureChatWebApp.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Signature;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.secureChatWebApp.mapper.UserMapper;
import com.secureChatWebApp.models.User;

@Repository()
public class UserDAO extends JdbcDaoSupport {

	@Autowired
	public UserDAO(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	public int createUser(String userName, String password, String rsaPubKeyEnc, String rsaPubKeySign) {
		String SQL = "insert into users (user_name, password, rsa_pub_key_enc, rsa_pub_key_sign) "
				+ "values (?, ?, ?, ?)";
		try {
			
			int inserted = this.getJdbcTemplate().update(SQL, userName, password, rsaPubKeyEnc, rsaPubKeySign);
			return inserted;
		} catch (DuplicateKeyException e) {
			// System.out.println("UserName exist before");
			return 0;
		}
	}

	public int authenticateUser(String userName, String hashedPassword) {
		String SQL = "select count(*) as valid from users where user_name = ? and password = ?";
		int authenticated = this.getJdbcTemplate().queryForObject(SQL, new Object[] { userName, hashedPassword },
				new RowMapper<Integer>() {

					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt("valid");
					}
				});
		return authenticated;
	}

	public List<String> getUsers(String userName,int offset, int limit) {
		List<String> users;
		
		// default limit = 10
		if (limit == -1) {
			String SQL = "select user_name from users where user_name != ? order by user_name LIMIT 10 ";
			users = this.getJdbcTemplate().queryForList(SQL,new Object[]{userName}, String.class);
		} else {
			if (offset == -1) {
				String SQL = "select user_name from users where user_name != ? order by user_name LIMIT ?";
				users = this.getJdbcTemplate().queryForList(SQL, new Object[] {userName, limit }, String.class);
			} else {
				String SQL = "select user_name from users where user_name != ? order by user_name LIMIT ? OFFSET ?";
				users = this.getJdbcTemplate().queryForList(SQL, new Object[] {userName, limit, offset }, String.class);

			}
		}
		return users;
	}

	public int deleteUser(String userName) {
		String SQL = "DELETE FROM users WHERE user_name= ?";
		int deleted = this.getJdbcTemplate().update(SQL, new Object[] { userName });
		return deleted;
	}

	public User getUser(String userName) {
		String SQL = "Select * from users where user_name=?";
		return this.getJdbcTemplate().queryForObject(SQL, new Object[] { userName }, new UserMapper());
	}
}
