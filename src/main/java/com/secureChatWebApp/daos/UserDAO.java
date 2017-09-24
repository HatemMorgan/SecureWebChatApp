package com.secureChatWebApp.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.secureChatWebApp.mapper.UserMapper;
import com.secureChatWebApp.models.User;

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

	public User getUserPublicKeys(String userName) {
		String SQL = "select user_name,rsa_pub_key_enc,rsa_pub_key_sign from users where user_name = ?";

		User user = this.getJdbcTemplate().queryForObject(SQL, new Object[] { userName }, new UserMapper());
		return user;
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

	public List<String> getUsers(int offset, int limit) {
		List<String> users;

		if (limit == -1) {
			String SQL = "select user_name from users order by user_name";
			users = this.getJdbcTemplate().queryForList(SQL, String.class);
		} else {
			if (offset == -1) {
				String SQL = "select user_name from users order by user_name LIMIT ?";
				users = this.getJdbcTemplate().queryForList(SQL, new Object[] { limit }, String.class);
			} else {
				String SQL = "select user_name from users order by user_name LIMIT ? OFFSET ?";
				users = this.getJdbcTemplate().queryForList(SQL, new Object[] { limit, offset  }, String.class);

			}
		}
		return users;
	}

	public int deleteUser(String userName) {
		String SQL = "DELETE FROM users WHERE user_name= ?";
		int deleted = this.getJdbcTemplate().update(SQL, new Object[] { userName });
		return deleted;
	}
}
