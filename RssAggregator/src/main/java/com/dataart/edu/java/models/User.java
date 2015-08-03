package com.dataart.edu.java.models;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.Set;

public class User
{
	public User(int id)
	{
		this.id = id;
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "SELECT * FROM users WHERE (id=" + id + ");";
				ResultSet rs = stat.executeQuery(query);
				if (rs.next())
				{
					this.username = rs.getString("name");
					this.fullName = URLDecoder.decode(rs.getString("full_name"), "UTF-8");
				}				
			} catch (SQLException | UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}				
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}				
	}
	
	public User(String username, String password, String confirm, String fullName)
	{
		this.username = username;
		this.password = password;
		this.confirm = confirm;
		this.fullName = fullName;
	}
	
	public boolean canCreate()
	{
		boolean result = false;
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "SELECT * FROM users WHERE (name=\"" + username + "\");";
				ResultSet rs = stat.executeQuery(query);
				result = !rs.next();
			}
		} catch (NamingException | SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}	
		return result;
	}
	
	public void create()
	{
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "INSERT INTO users (name, pass, full_name) VALUES (" +
					"\"" + URLEncoder.encode(username, "UTF-8") + "\", " +
					"\"" + DigestUtils.shaHex(password) + "\", " +
					"\"" + URLEncoder.encode(fullName, "UTF-8") + "\");";
				stat.executeUpdate(query);
				query = "SELECT LAST_INSERT_ID();";
				ResultSet rs = stat.executeQuery(query);
				rs.next();
				id = rs.getInt(1);
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException | SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public boolean authenticate()
	{
		boolean result = false;
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "SELECT * FROM users WHERE (name=\"" +
					URLEncoder.encode(username, "UTF-8") + "\");";
				ResultSet rs = stat.executeQuery(query);
				if (rs.next())
				{
					id = rs.getInt("id");
					fullName = URLDecoder.decode(rs.getString("full_name"), "UTF-8");
					result = DigestUtils.shaHex(password).equals(rs.getString("pass"));
				}
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException | SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public List<Channel> getChannels()
	{
		List<Channel> channels = new LinkedList<>();
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement selectUrl = conn.createStatement();
				String query = "SELECT id, url FROM channels where (id IN (" + 
					"SELECT	channel_id FROM user_channel WHERE (user_id=" +
					id + ")));";
				ResultSet rs = selectUrl.executeQuery(query);
				int channelId;
				while (rs.next())
				{
					channelId = rs.getInt("id");
					Statement selectName = conn.createStatement();
					query = "SELECT name FROM user_channel WHERE (channel_id=" +
						channelId + " AND user_id=" + id + ");";
					ResultSet nameSet = selectName.executeQuery(query);
					nameSet.next();
					channels.add(new Channel(
						id, nameSet.getString("name"), rs.getString("url")));
				}
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return channels;
	}
	
	public String getFullName()
	{
		return fullName;
	}
	
	public void addChannel(String channel)
	{
		rssChannels.add(channel);
	}
	
	public void deleteChannel(String channel)
	{
		
	}

	public int getId()
	{
		return id;
	}
	
	private int id;
	private String username;
	private String password;
	private String confirm;
	private String fullName;
	private Set<String> rssChannels;
	
	private static Logger logger = Logger.getLogger(User.class.getName());	
}
