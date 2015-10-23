package com.dataart.edu.java.dao;

import com.dataart.edu.java.auxiliary.DataSourceProvider;
import com.dataart.edu.java.domain.User;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UserDao
{
	public User getUser(String name)
	{
		User result = null;
		try{
			SqlRowSet rs = JDBC_TEMPLATE.queryForRowSet(GET_USERS_BY_NAME,
				URLEncoder.encode(name, "UTF-8"));
			if (rs.next())
			{
				result = (new User.Builder()).setId(rs.getInt("id")).setUsername(name).
					setFullName(URLDecoder.decode(rs.getString("full_name"), "UTF-8")).build();
			}
		}
		catch (UnsupportedEncodingException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public String getPassword(String name)
	{
		String result = null;
		try {
			result = JDBC_TEMPLATE.queryForObject(GET_PASSWORD,
				new Object[]{URLEncoder.encode(name, "UTF-8")}, String.class);
		} catch (EmptyResultDataAccessException ee) {
		} catch (UnsupportedEncodingException ue) {
			LOGGER.log(Level.SEVERE, ue.getMessage(), ue);
		}
		return result;
	}
	
	public String getSalt(String name)
	{
		String result = null;
		try {
			result = JDBC_TEMPLATE.queryForObject(GET_SALT,
				new Object[]{URLEncoder.encode(name, "UTF-8")}, String.class);
		} catch (EmptyResultDataAccessException ee) {
		} catch (UnsupportedEncodingException ue) {
			LOGGER.log(Level.SEVERE, ue.getMessage(), ue);
		}
		return result;
	}
	
	public void saveUser(User user, String pass, String salt)
	{
		try {
			JDBC_TEMPLATE.update(INSERT_USER, 
				URLEncoder.encode(user.getUsername(), "UTF-8"), pass, salt,
				URLEncoder.encode(user.getFullName(), "UTF-8")
			);
		} catch (UnsupportedEncodingException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private static final JdbcTemplate JDBC_TEMPLATE =
		new JdbcTemplate(DataSourceProvider.getDataSource());
	private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());
	
	private static final String GET_USERS_BY_NAME =
		"SELECT id, pass, full_name FROM users WHERE name=?";
	private static final String GET_PASSWORD = "SELECT pass FROM users WHERE name=?";
	private static final String GET_SALT = "SELECT salt FROM users WHERE name=?";
	private static final String INSERT_USER =
		"INSERT INTO users(name, pass, salt, full_name) VALUES(?, ?, ?, ?)";
}
