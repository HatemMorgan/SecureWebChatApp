package com.secureChatWebApp.configs;

import java.io.IOException;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

//Load to Environment.
@Component
public class DatabaseConfigs {

	@Bean(name = "dataSource")
	public DataSource getDataSource() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(properties.getProperty("jdbc.mysql.database-driver"));
		dataSource.setUrl(properties.getProperty("jdbc.mysql.url"));
		dataSource.setUsername(properties.getProperty("jdbc.mysql.userName"));
		dataSource.setPassword(properties.getProperty("jdbc.mysql.password"));

		System.out.println("## getDataSource: " + dataSource);

		return dataSource;
	}

}
