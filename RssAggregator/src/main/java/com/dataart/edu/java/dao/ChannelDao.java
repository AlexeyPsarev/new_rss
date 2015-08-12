package com.dataart.edu.java.dao;

import com.dataart.edu.java.datasource.DataSourceProvider;
import com.dataart.edu.java.domain.Channel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ChannelDao
{
	public void save(Channel ch)
	{
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				conn.setAutoCommit(false);
				Statement stat = conn.createStatement();
				try {
					String queryStr = "SELECT * FROM channels WHERE (url=?);";
					PreparedStatement findByUrlQuery = conn.prepareStatement(queryStr);
					findByUrlQuery.setString(1, ch.getUrl());
					ResultSet urlSet = findByUrlQuery.executeQuery();
					if (!urlSet.next())
					{
						queryStr = "INSERT INTO channels(url) VALUES (?);";
						PreparedStatement insertUrlQuery = conn.prepareStatement(queryStr);
						insertUrlQuery.setString(1, ch.getUrl());
						insertUrlQuery.executeUpdate();
						queryStr = "SELECT LAST_INSERT_ID();";
						ResultSet idSet = stat.executeQuery(queryStr);
						idSet.next();
						ch.setId(idSet.getInt(1));
					}
					else
						ch.setId(urlSet.getInt("id"));

					queryStr = "INSERT IGNORE INTO user_channel VALUES (?, ?, ?);";
					PreparedStatement insertQuery = conn.prepareStatement(queryStr);
					insertQuery.setInt(1, ch.getUserId());
					insertQuery.setInt(2, ch.getId());
					insertQuery.setString(3, ch.getName());
					insertQuery.executeUpdate();
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
	
	public void delete(Channel ch)
	{
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				conn.setAutoCommit(false);
				try {
					String queryStr = "DELETE FROM user_channel WHERE (user_id=?)" +
						" AND channel_id=(SELECT id FROM channels WHERE (url=?));";
					PreparedStatement query = conn.prepareStatement(queryStr);
					query.setInt(1, ch.getUserId());
					query.setString(2, ch.getUrl());
					query.executeUpdate();
					queryStr = "DELETE FROM user_news WHERE (user_id=?) AND" +
						" (news_id IN (SELECT guid FROM news WHERE " +
						"(channel_id=(SELECT id FROM channels WHERE (url=?)))));";
					query = conn.prepareStatement(queryStr);
					query.setInt(1, ch.getUserId());
					query.setString(2, ch.getUrl());
					query.executeUpdate();
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

	public int getId(Channel ch)
	{
		int result = 0;
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "SELECT id FROM channels WHERE (url=?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setString(1, ch.getUrl());
				ResultSet rs = query.executeQuery();
				if (rs.next())
					result = rs.getInt("id");
			}catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return result;
	}
	
	public List<Channel> getChannels(int uid)
	{
		List<Channel> channels = new LinkedList<>();
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "SELECT id, url FROM channels where (id IN (" + 
					"SELECT	channel_id FROM user_channel WHERE (user_id=?)));";
				PreparedStatement selectUrlQuery = conn.prepareStatement(queryStr);
				selectUrlQuery.setInt(1, uid);
				ResultSet rs = selectUrlQuery.executeQuery();
				int channelId;
				while (rs.next())
				{
					channelId = rs.getInt("id");
					queryStr = "SELECT name FROM user_channel WHERE (channel_id=?" +
						" AND user_id=?);";
					PreparedStatement selectNameQuery = conn.prepareStatement(queryStr);
					selectNameQuery.setInt(1, channelId);
					selectNameQuery.setInt(2, uid);
					ResultSet nameSet = selectNameQuery.executeQuery();
					nameSet.next();
					Channel ch = new Channel();
					ch.setId(channelId);
					ch.setName(nameSet.getString("name"));
					ch.setUrl(rs.getString("url"));
					ch.setUserId(uid);
					channels.add(ch);
				}
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return channels;
	}
	
	private static Logger logger = Logger.getLogger(ChannelDao.class.getName());
}
