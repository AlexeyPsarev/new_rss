package com.dataart.edu.java.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class NewsNode
{
	public NewsNode(int userId, String guid, String title, String link,
		String description, String pubDate, int channelId)
	{
		this.userId = userId;
		this.guid = guid;
		this.title = title;
		this.link = link;
		this.description = description;
		this.pubDate = pubDate;
		this.channelId = channelId;
	}
	
	public NewsNode(int userId, String guid)
	{
		this.userId = userId;
		this.guid = guid;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getPubDate()
	{
		return pubDate;
	}
	
	public boolean isRead()
	{
		boolean result = false;
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "SELECT is_read FROM user_news WHERE(user_id=" +
					userId + " AND news_id=\"" + guid + "\");";
				ResultSet rs = stat.executeQuery(query);
				if(rs.next())
					result = rs.getBoolean(1);
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public String getGuid()
	{
		return guid;
	}
	
	public void save()
	{
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				conn.setAutoCommit(false);
				Statement stat = conn.createStatement();
				try {
					String dateAttr;
					String dateVal;
					/* The DateTime obtained using the Date or LocalDateTime or 
						Calendar classes is an hour ahead. So, the TIMESTAMP field
						is set by the database by default. */
					if (pubDate.isEmpty())
						dateAttr = dateVal = "";
					else
					{
						dateAttr = "pub_date, ";
						dateVal = "\"" + pubDate + "\", ";
					}
					String query = "INSERT IGNORE INTO news (guid, title, link, description, " + dateAttr + "channel_id) " +
						"VALUES (\"" + guid + "\", \"" + title + "\", \"" + link +
						"\", \"" + description + "\", " + dateVal + channelId + ");" ;
					stat.executeUpdate(query);
					query = "INSERT INTO user_news VALUES(" + userId + ", \"" +
						guid + "\", false);";
					stat.executeUpdate(query);
					conn.commit();
				} catch (SQLException ex) {
					conn.rollback();
					logger.log(Level.SEVERE, ex.getMessage(), ex);
				} finally {
					conn.setAutoCommit(true);
				}
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public void delete()
	{
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "DELETE FROM user_news WHERE (user_id=" + userId +
					" AND news_id=\"" + guid + "\");";
				stat.executeUpdate(query);			
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public void updateReadAttribute(boolean isRead)
	{
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "UPDATE user_news SET is_read=" + isRead +
					" WHERE (user_id=" + userId + " AND news_id=\"" + guid + "\");";
				stat.executeUpdate(query);
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private int userId;
	private String guid;
	private String title;
	private String link;
	private String description;
	private String pubDate;
	private int channelId;
	
	private static Logger logger = Logger.getLogger(NewsNode.class.getName());
}
