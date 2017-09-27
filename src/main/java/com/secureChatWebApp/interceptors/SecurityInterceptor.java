package com.secureChatWebApp.interceptors;

import java.io.IOException;
import java.security.Signature;
import java.security.SignatureSpi;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.secureChatWebApp.daos.UserDAO;
import com.secureChatWebApp.models.User;
import com.secureChatWebApp.utilites.RSAUtility;
import com.secureChatWebApp.utilites.SignaturesUtility;

@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {

	private UserDAO userDAO;
	private short delay;

	@Autowired
	public SecurityInterceptor(UserDAO userDAO) throws IOException {
		this.userDAO = userDAO;

		// load request delay from configs file
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/configs.properties"));
		delay = Short.parseShort(properties.getProperty("config.request.delay"));
	}

	/**
	 * Authenticate Requests by checking that x-acess-token header is valid
	 * 
	 * x-acess-token = {userName}:{date}:signature
	 * 
	 * signature = sign(prvClient,{HashedPassword}:{date})
	 * 
	 * where date is the date time of sending request and request will be
	 * rejected if this date time is less than current date by delay attribute
	 * 
	 * also its verifies signature after loading user's stored hashed password
	 * from database
	 * 
	 * if the date is valid and signature is valid then the request is
	 * authenticated
	 * 
	 * there are two boolean variables isAuthenticated and isValidUser which
	 * are added as attributes to request handler to take an action.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("in the logging Interceptor");

		boolean isAuthenticated;
		boolean isValidUser;

		// token = {userName}:{date}:signature
		// signature = sign(prvClient,{HashedPassword}:{date})
		String tokenHeader = request.getHeader("x-acess-token");

		String[] token = tokenHeader.split(":");
		String userName = token[0];
		String date = token[1];
		String signature = token[2];

		// get hashed password of user with user_name {userName}
		User user = userDAO.getUser(userName);

		// check that hashed password is not null to make sure that user
		// with user_name {userName} is a valid user that did register before
		if (user == null) {
			isValidUser = false;
			isAuthenticated = false;
		} else {
			// valid registered user
			isValidUser = true;
			// check that request date is valid
			if (!isDateValid(date)) {
				// not a valid date so request is not authenticated
				isAuthenticated = false;
			} else {
				// valid request date so check that signature is valid
				// get plain message
				String plainMessage = user.getPassword() + ":" + date;

				boolean verfied = SignaturesUtility.performVerification(plainMessage, signature,
						RSAUtility.reConstructPublicKey(user.getRsaPubKeySign()));

				isAuthenticated = verfied ? true : false;
			}
		}
		// set attributes of request to booleans isValidUser and isAuthenticated
		// in order to allow handlers to take actions
		request.setAttribute("isAuthenticated", isAuthenticated);
		request.setAttribute("isValidUser", isValidUser);
		return true;
	}

	/**
	 * Check that request date is valid by checking that the difference between
	 * current date and request date is smaller than delay attribute
	 * 
	 * This check will prevent Replay attacks
	 * 
	 * @param utcEpochDate
	 *            request date which is in UTC timezone and in epoch format eg.
	 *            1538064400147
	 * @return
	 */
	public boolean isDateValid(String utcEpochDate) {
		// getting Calendar set to UTC timeZone
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// set calendar time to input utcEpochDate
		cal1.setTime(new Date(Long.parseLong(utcEpochDate)));

		// creating another calendar in UTC timeZone and set it to current time
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal2.setTime(new Date());

		// calculate difference in minutes, hours, days and years
		long diffMinutes = cal2.get(Calendar.MINUTE) - cal1.get(Calendar.MINUTE);
		long diffHours = cal2.get(Calendar.HOUR) - cal1.get(Calendar.HOUR);
		int diffInDays = cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH);
		int diffInYears = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);

		// System.out.println(diffMinutes+" "+diffHours+" "+diffInDays+"
		// "+diffInYears);
		if (diffInYears == 0 && diffInDays == 0 && diffHours == 0 && diffMinutes < delay)
			return true;

		return false;
	}

	/**
	 * // read body in the interceptor but you will not be able recover it //
	 * again
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String readRequestBody(HttpServletRequest request) throws IOException {
		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		System.out.println(body);
		return body;
	}

	public static void main(String[] args) throws IOException {
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal2.setTime(new Date());
		cal2.add(Calendar.YEAR, 1);
		System.out.println(cal2.getTimeInMillis());
		SecurityInterceptor s = new SecurityInterceptor(null);
		System.out.println(s.isDateValid(cal2.getTimeInMillis() + ""));
	}
}
