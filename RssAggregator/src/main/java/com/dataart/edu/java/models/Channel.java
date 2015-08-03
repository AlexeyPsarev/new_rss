package com.dataart.edu.java.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Channel
{
	public Channel(String srcUrl)
	{
		url = srcUrl;
		name = "";
	}
		
	public Channel(int uid, String channelName, String srcUrl)
	{
		userId = uid;
		url = srcUrl;
		name = channelName;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public String getName()
	{
		return name;
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
					String query = "SELECT * FROM channels WHERE (url=\"" + url + "\");";
					ResultSet urlSet = stat.executeQuery(query);
					if (!urlSet.next())
					{
						query = "INSERT INTO channels(url) VALUES (\"" + url + "\");";
						stat.executeUpdate(query);
						query = "SELECT LAST_INSERT_ID();";
						ResultSet idSet = stat.executeQuery(query);
						idSet.next();
						id = idSet.getInt(1);
					}
					else
						id = urlSet.getInt("id");
					
					query = "INSERT IGNORE INTO user_channel VALUES (" +
						userId + ", " + id + ", \"" + name + "\");";
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
				conn.setAutoCommit(false);
				Statement stat = conn.createStatement();
				try {
					String query = "DELETE FROM user_channel WHERE (user_id=" + userId +
						" AND channel_id=(SELECT id FROM channels WHERE (url=\"" + url + "\")));";
					stat.executeUpdate(query);
					query = "DELETE FROM user_news WHERE (user_id=" + userId +
						") AND (news_id IN (SELECT guid FROM news WHERE " +
						"(channel_id=(SELECT id FROM channels WHERE (url=\"" + url + "\")))));";
					stat.executeUpdate(query);
					conn.commit();
				} catch (SQLException ex) {
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
	
	public int getId()
	{
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				String query = "SELECT id FROM channels WHERE (url=\"" + url + "\");";
				ResultSet rs = stat.executeQuery(query);
				if (rs.next())
					id = rs.getInt("id");
			}catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return id;
	}
	
	public void setUrl(String newUrl)
	{
		url = newUrl;
	}
	
	public void update()
	{
		try {
			URL address = new URL(url);
			InputStream stream = address.openStream();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(stream);
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			String xpathStr = "/rss/channel/item";
			NodeList result = (NodeList) xpath.evaluate(xpathStr, doc, XPathConstants.NODESET);
			int length = result.getLength();
			NewsNode node;
			for (int j = 0; j < length; ++j)
			{
				xpathStr = "guid";
				String guid = URLEncoder.encode(xpath.evaluate(xpathStr, result.item(j)), "UTF-8");
				xpathStr = "title";
				String title = xpath.evaluate(xpathStr, result.item(j));
				xpathStr = "link";
				String link = xpath.evaluate(xpathStr, result.item(j));
				xpathStr = "description";
				String description = xpath.evaluate(xpathStr, result.item(j));
				xpathStr = "pubDate";
				String pubDate = xpath.evaluate(xpathStr, result.item(j));
				String formattedDate;
				SimpleDateFormat sqlToDate;
				SimpleDateFormat dateToStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					sqlToDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzzzz", Locale.ENGLISH);
					Date date = sqlToDate.parse(pubDate);
					formattedDate = dateToStr.format(date);
				} catch (ParseException ex) {
					try {
						sqlToDate = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzzzz", Locale.ENGLISH);
						Date date = sqlToDate.parse(pubDate);
						formattedDate = dateToStr.format(date);
					} catch (ParseException ex1) {
						String s = (new Date()).toString();
						formattedDate = "";
					}
				}
				node = new NewsNode(
					userId, guid, title, link, description, formattedDate, getId());
				node.save();
			}
		} catch (IOException | ParserConfigurationException |
			SAXException | XPathExpressionException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	private int id;
	private int userId;
	private	String url;
	private String name;
	
	private static Logger logger = Logger.getLogger(Channel.class.getName());
}
