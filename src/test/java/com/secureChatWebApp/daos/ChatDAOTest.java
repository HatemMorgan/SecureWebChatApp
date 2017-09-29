package com.secureChatWebApp.daos;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import com.secureChatWebApp.exceptions.DatabaseException;

public class ChatDAOTest {

	ChatDAO chatDAO;
	UserDAO userDAO;

	@Before
	public void setup() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(properties.getProperty("jdbc.mysql.database-driver"));
		dataSource.setUrl(properties.getProperty("jdbc.mysql.url"));
		dataSource.setUsername(properties.getProperty("jdbc.mysql.userName"));
		dataSource.setPassword(properties.getProperty("jdbc.mysql.password"));

		chatDAO = new ChatDAO(dataSource);
		userDAO = new UserDAO(dataSource);
	}

	@Test
	public void testCreation() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		int inserted = chatDAO.create("test1", "test2",
				"iNUTHr3Tuu1HS8ihTbLdj5WMIx4URGjXYUB5cMSnJ3tAPEEEW9DpAjAMbemBsFTfq1xz/QbZEn/ddMLSycTofQ==");

		assertEquals("Failure, Cannot insert a new chat between two existing users", 1, inserted);

		int inserted2 = chatDAO.create("test2", "test1",
				"iNUTHr3Tuu1HS8ihTbLdj5WMIx4URGjXYUB5cMSnJ3tAPEEEW9DpAjAMbemBsFTfq1xz/QbZEn/ddMLSycTofQ==");
		assertEquals("Failure, Cannot insert a new chat between two existing users in reverse order", 1, inserted2);

		chatDAO.deleteChat("test1", "test2");
		chatDAO.deleteChat("test2", "test1");
		userDAO.deleteUser("test1");
		userDAO.deleteUser("test2");
	}

	@Test(expected = DatabaseException.class)
	public void testCreateUserWithInvalidReceiver() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		chatDAO.create("test1", "test",
				"iNUTHr3Tuu1HS8ihTbLdj5WMIx4URGjXYUB5cMSnJ3tAPEEEW9DpAjAMbemBsFTfq1xz/QbZEn/ddMLSycTofQ==");

		userDAO.deleteUser("test1");
	}

	@Test
	public void testDeleteChat() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		chatDAO.create("test1", "test2",
				"iNUTHr3Tuu1HS8ihTbLdj5WMIx4URGjXYUB5cMSnJ3tAPEEEW9DpAjAMbemBsFTfq1xz/QbZEn/ddMLSycTofQ==");

		int deleted = chatDAO.deleteChat("test1", "test2");
		assertEquals("Failure, Cannot delete an added chat", 1, deleted);

		int deleted2 = chatDAO.deleteChat("test1", "test2");
		assertEquals("Failure, delete chat that wasnot added before!", 0, deleted2);

		userDAO.deleteUser("test1");
		userDAO.deleteUser("test2");

	}

	@Test
	public void testGetEncryptedChatKey() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		chatDAO.create("test1", "test2",
				"iNUTHr3Tuu1HS8ihTbLdj5WMIx4URGjXYUB5cMSnJ3tAPEEEW9DpAjAMbemBsFTfq1xz/QbZEn/ddMLSycTofQ==");

		String encryptedKey = chatDAO.getEncryptedChatKey("test1", "test2");
		assertEquals("Failure, Wrong fetched encrypted chat key",
				"iNUTHr3Tuu1HS8ihTbLdj5WMIx4URGjXYUB5cMSnJ3tAPEEEW9DpAjAMbemBsFTfq1xz/QbZEn/ddMLSycTofQ==",
				encryptedKey);

		chatDAO.deleteChat("test1", "test2");
		userDAO.deleteUser("test1");
		userDAO.deleteUser("test2");

	}

}
