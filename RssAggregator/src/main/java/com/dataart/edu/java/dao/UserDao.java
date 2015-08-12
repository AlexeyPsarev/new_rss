package com.dataart.edu.java.dao;

import com.dataart.edu.java.datasource.DataSourceProvider;
import com.dataart.edu.java.domain.User;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class UserDao
{
	public User getUser(int id)
	{
		User result = null;
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "SELECT * FROM users WHERE (id=?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setInt(1, id);
				ResultSet rs = query.executeQuery();
				if (rs.next())
				{
					result = new User();
					result.setId(id);
					result.setUsername(URLDecoder.decode(
						rs.getString("name"), "UTF-8"));
					result.setFullName(URLDecoder.decode(
						rs.getString("full_name"), "UTF-8"));
				}
			} catch (SQLException | UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public User getUser(String name)
	{
		User result = null;
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "SELECT * FROM users WHERE (name=?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setString(1, URLEncoder.encode(name, "UTF-8"));
				ResultSet rs = query.executeQuery();
				if (rs.next())
				{
					result = new User();
					result.setId(rs.getInt("id"));
					result.setUsername(name);
					result.setFullName(URLDecoder.decode(rs.getString("full_name"), "UTF-8"));
				}
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException | SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public User getUser(String name, String password)
	{
		User result = null;
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "SELECT id, full_name FROM users WHERE (name=? AND pass=?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setString(1, URLEncoder.encode(name, "UTF-8"));
				query.setString(2, password);
				ResultSet rs = query.executeQuery();
				if (rs.next())
				{
					result = new User();
					result.setId(rs.getInt("id"));
					result.setUsername(name);
					result.setFullName(URLDecoder.decode(rs.getString("full_name"), "UTF-8"));
				}
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException | SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public void saveUser(User user)
	{
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String queryStr = "INSERT INTO users (name, pass, full_name)" +
					" VALUES (?, ?, ?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setString(1, URLEncoder.encode(user.getUsername(), "UTF-8"));
				query.setString(2, user.getPassword());
				query.setString(3, URLEncoder.encode(user.getFullName(), "UTF-8"));
				query.executeUpdate();
				queryStr = "SELECT LAST_INSERT_ID();";
				ResultSet rs = stat.executeQuery(queryStr);
				rs.next();
				user.setId(rs.getInt(1));
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException | SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private static Logger logger = Logger.getLogger(UserDao.class.getName());
}
