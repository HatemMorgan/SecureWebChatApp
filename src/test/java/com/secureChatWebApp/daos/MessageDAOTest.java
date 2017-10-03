package com.secureChatWebApp.daos;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.activity.InvalidActivityException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.secureChatWebApp.exceptions.DatabaseException;
import com.secureChatWebApp.models.Inbox;
import com.secureChatWebApp.models.Message;

public class MessageDAOTest {
	UserDAO userDAO;
	MessageDAO messageDAO;
	ChatDAO chatDAO;
	
	@Before
	public void setup() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(properties.getProperty("jdbc.mysql.database-driver"));
		dataSource.setUrl(properties.getProperty("jdbc.mysql.url"));
		dataSource.setUsername(properties.getProperty("jdbc.mysql.userName"));
		dataSource.setPassword(properties.getProperty("jdbc.mysql.password"));

		userDAO = new UserDAO(dataSource);
		messageDAO = new MessageDAO(dataSource);
		chatDAO = new ChatDAO(dataSource);
	}

	@Test
	public void testCreateMessage() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		Message insertedMessage1 = messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
			
		assertFalse("Failure, Cannot create a new message",insertedMessage1.equals(null));

		Message insertedMessage2 = messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		
		assertFalse("Failure, Cannot create a new message",insertedMessage2.equals(null));

		messageDAO.deleteMessage("test1", "test2");

	}

	@Test
	public void testDumpMessages() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		messageDAO.createMessage("test1", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");

		List<Message> dumbedMessagesList = messageDAO.dumpMessages();

		boolean flag1 = false;
		boolean flag2 = false;
		for(Message message :dumbedMessagesList ){
			
			if(message.getText().equals("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk="))
				flag1 = true;
			
			if(message.getText().equals("PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk="))
				flag2 = true;
		}
		
		assertEquals("Failure, Message: <PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=> was not found", true,flag1);
		assertEquals("Failure, Message: <PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=> was not found", true,flag2);


		messageDAO.deleteMessage("test1", "test2");

	}

	@Test
	public void testGetOldMessages() throws DatabaseException, InterruptedException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk1=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk2=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk3=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk4=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

		List<Message> oldMessages = messageDAO.getOldMessages("test1", "test2", 2, 4);
		
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		for(Message message :oldMessages ){
			
			if(message.getText().equals("PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk4="))
				flag1 = true;
			
			if(message.getText().equals("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk3="))
				flag2 = true;
			
			if(message.getText().equals("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk2="))
				flag3 = true;
			
			if(message.getText().equals("PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk1="))
				flag4 = true;
		}
		
		assertEquals("Failure, Message: <PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk4=> was not found", true,flag1);
		assertEquals("Failure, Message: <PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk3=> was not found", true,flag2);
		assertEquals("Failure, Message: <PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk2=> was not found", true,flag3);
		assertEquals("Failure, Message: <PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk1=> was not found", true,flag4);

		messageDAO.deleteMessage("test1", "test2");

	}

	@Test
	public void testGetInbox() throws DatabaseException, InterruptedException, InvalidActivityException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test3", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");
		
		chatDAO.create("test1", "test2","BDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6");
		chatDAO.create("test1", "test3","yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSasasdejoirwf57qwe5dd");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

		messageDAO.createMessage("test3", "test1", "oiddsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		messageDAO.createMessage("test3", "test1", "sdddadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		messageDAO.createMessage("test2", "test1", "21sdddadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

		List<Inbox> inbox = messageDAO.getUserInbox("test1");

		assertEquals("Failure,Wrong Inbox Sender fetched", "test3", inbox.get(1).getSender());
		assertEquals("Failure,Wrong Inbox message fetched", "sdddadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=",
				inbox.get(1).getMessage());
		assertEquals("Failure,Wrong Inbox encryptedChatKey fetched", "yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSasasdejoirwf57qwe5dd",
				inbox.get(1).getEncryptedChatKey());
		
		assertEquals("Failure,Wrong Inbox Sender fetched","test2" ,inbox.get(0).getSender());
		assertEquals("Failure,Wrong Inbox message fetched", "21sdddadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=",
				inbox.get(0).getMessage());
		assertEquals("Failure,Wrong Inbox encryptedChatKey fetched", "BDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6",
				inbox.get(0).getEncryptedChatKey());

		messageDAO.deleteMessage("test1", "test2");
		messageDAO.deleteMessage("test1", "test3");
		chatDAO.deleteChat("test1", "test2");
		chatDAO.deleteChat("test1", "test3");	

		userDAO.deleteUser("test3");
	}

	@Test
	public void testSetMessagesDelievered() throws DatabaseException, InterruptedException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test1", "test2", "PTKZasdEAasdaGwTW0+EiT22LVQscoRk7rxVasdadsk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "PTKZasdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");
		Thread.sleep(1000);
		messageDAO.createMessage("test2", "test1", "asdadsdEAasadawTW0+EiT22LVQscoRk7rxVasdasdk=");

		int updated = messageDAO.setMessagesDelivered("test2", "test1");
		assertEquals("Failure,Message not updated", 2, updated);

		List<Message> oldMessages = messageDAO.getOldMessages("test2", "test1", -1, -1);
		System.out.println(oldMessages);
		assertEquals("Failure, Message not updated Correctly", true, oldMessages.get(0).getDelivered());
		assertEquals("Failure, Message not updated Correctly", true, oldMessages.get(1).getDelivered());

		messageDAO.deleteMessage("test1", "test2");

	}

	@Test
	public void testDeleteMessage() throws DatabaseException {
		userDAO.createUser("test1", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		userDAO.createUser("test2", "2123ejdq124fa32",
				"KeyiOAhUg+yy2fVcCxeBDFwMPA1y5mIzSwj3UMiyuWQ3YmBJqqPSgNSnRmx+VXu/nhuNzGVC8gczZXy3HtP6IpFtQ==",
				"Keytuccq/Y0hfqtxyxtQ0d7MCLikeO5yyoAC0yAoMsHLl5ElRfiIX5HRdTYS4MC92iYVAwVnB0lDgSPLhVWttR4UQ==");

		messageDAO.createMessage("test1", "test2", "PTKZ7aGJ74EAmHGwTW0+EiT22LVQtscoRk7rxVfMNVk=");

		int deleted = messageDAO.deleteMessage("test1", "test2");
		assertEquals("Failure, Cannot delete an added message", 1, deleted);

	}

	@After
	public void tearDown() {
		userDAO.deleteUser("test1");
		userDAO.deleteUser("test2");
	}

}
