package com.dataart.edu.java.dao;

import com.dataart.edu.java.datasource.DataSourceProvider;
import com.dataart.edu.java.domain.NewsNode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class NewsNodeDao
{
	public final int NEWS_PER_PAGE = 10; // used in NewsNodeManager
	private final int MAX_AGE = 7;
	private final String COUNT_QUERY_BEGIN = "SELECT COUNT(guid) FROM news WHERE" +
		" (guid IN (SELECT news_id FROM user_news WHERE (user_id=:user_id))";
	private final String SELECT_QUERY_BEGIN = "SELECT * FROM news JOIN user_news" +
		" ON ((guid=news_id) AND (user_id=:user_id)";
	private static Logger logger = Logger.getLogger(NewsNodeDao.class.getName());

	public int getNewsCount(int userId, int channelId,
		String dateSort, String keyword, String beginDate, String endDate)
	{
		int newsCount = 0;
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			StringBuilder conditionBuilder = new StringBuilder();
			NamedParameterJdbcTemplate jdbcTemplate =
				new NamedParameterJdbcTemplate(ds);
			MapSqlParameterSource paramSource = new MapSqlParameterSource();
			paramSource.addValue("user_id", userId);
			if ((keyword != null) && !(keyword.isEmpty()))
			{
				conditionBuilder.append(
					" AND (title LIKE :keyword OR description LIKE :keyword)");
				paramSource.addValue("keyword", "%" + keyword + "%");
			}
			if ((beginDate != null) && !(beginDate.isEmpty()))
			{
				conditionBuilder.append(" AND pub_date>=:begin_date");
				paramSource.addValue("begin_date", beginDate);
			}
			if ((endDate != null) && !(endDate.isEmpty()))
			{
				conditionBuilder.append(" AND pub_date<=:end_date");
				paramSource.addValue("end_date", endDate);
			}
			if (channelId != 0)
			{
				conditionBuilder.append(" AND channel_id=:channel_id");
				paramSource.addValue("channel_id", channelId);
			}
			conditionBuilder.append(")");
			SqlRowSet countSet = jdbcTemplate.queryForRowSet(
				COUNT_QUERY_BEGIN + conditionBuilder.toString(), paramSource);
			countSet.next();
			newsCount = countSet.getInt(1);
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return newsCount;
	}
	
	public List<NewsNode> getNews(int userId, int channelId, int newsBegin,
		String dateSort, String keyword, String beginDate, String endDate)
	{
		List<NewsNode> list = new LinkedList<>();
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			StringBuilder conditionBuilder = new StringBuilder();
			NamedParameterJdbcTemplate jdbcTemplate =
				new NamedParameterJdbcTemplate(ds);
			MapSqlParameterSource paramSource = new MapSqlParameterSource();
			paramSource.addValue("user_id", userId);
			if ((keyword != null) && !(keyword.isEmpty()))
			{
				conditionBuilder.append(
					" AND (title LIKE :keyword OR description LIKE :keyword)");
				paramSource.addValue("keyword", "%" + keyword + "%");
			}
			if ((beginDate != null) && !(beginDate.isEmpty()))
			{
				conditionBuilder.append(" AND pub_date>=:begin_date");
				paramSource.addValue("begin_date", beginDate);
			}
			if ((endDate != null) && !(endDate.isEmpty()))
			{
				conditionBuilder.append(" AND pub_date<=:end_date");
				paramSource.addValue("end_date", endDate);
			}
			if (channelId != 0)
			{
				conditionBuilder.append(" AND channel_id=:channel_id");
				paramSource.addValue("channel_id", channelId);
			}
			conditionBuilder.append(")");
			 
			conditionBuilder.append(" ORDER BY pub_date");
			if (!("Asc".equals(dateSort)))
				conditionBuilder.append(" DESC");
			conditionBuilder.append(" LIMIT ").append(newsBegin).append(", ").append(NEWS_PER_PAGE);
			SqlRowSet newsSet = jdbcTemplate.queryForRowSet(
				SELECT_QUERY_BEGIN + conditionBuilder.toString(), paramSource);
			while (newsSet.next())
			{
				String pubDate = newsSet.getDate("pub_date").toString();
				String pubTime = newsSet.getTime("pub_date").toString();
				NewsNode node = new NewsNode();
				node.setUserId(userId);
				node.setGuid(newsSet.getString("guid"));
				node.setTitle(newsSet.getString("title"));
				node.setLink(newsSet.getString("link"));
				node.setDescription(newsSet.getString("description"));
				node.setPubDate(pubDate + " " + pubTime);
				node.setChannelId(channelId);
				node.setRead(newsSet.getBoolean("is_read"));
				list.add(node);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return list;
	}

	public void save(NewsNode node)
	{
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				conn.setAutoCommit(false);
				try {
					/* The DateTime obtained using the Date or LocalDateTime or 
						Calendar classes is an hour ahead. So, the TIMESTAMP field
						is set by the database by default. */
					String queryStr;
					String pubDate = node.getPubDate();
					if (pubDate.isEmpty())
					{
						queryStr = "INSERT IGNORE INTO news (guid, title, link, description, " +
						"channel_id) " + "VALUES (?, ?, ?, ?, ?);";
					}
					else
					{
						queryStr = "INSERT IGNORE INTO news (guid, title, link, description, " +
						"pub_date, channel_id) " + "VALUES (?, ?, ?, ?, ?, ?);";
					}
										
					PreparedStatement query = conn.prepareStatement(queryStr);
					query.setString(1, node.getGuid());
					query.setString(2, node.getTitle());
					query.setString(3, node.getLink());
					query.setString(4, node.getDescription());
					if (pubDate.isEmpty())
					{
						query.setInt(5, node.getChannelId());
					}
					else
					{
						query.setString(5, pubDate);
						query.setInt(6, node.getChannelId());
					}
					query.executeUpdate();

					queryStr = "INSERT INTO user_news VALUES(?, ?, ?);";
					query = conn.prepareStatement(queryStr);
					query.setInt(1, node.getUserId());
					query.setString(2, node.getGuid());
					query.setBoolean(3, node.isRead());
					query.executeUpdate();
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
	
	public void update(NewsNode node)
	{
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "UPDATE user_news SET is_read=?" +
					" WHERE (user_id=? AND news_id=?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setBoolean(1, node.isRead());
				query.setInt(2, node.getUserId());
				query.setString(3, node.getGuid());
				query.executeUpdate();
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public void delete(NewsNode node)
	{
		try {
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				String queryStr = "DELETE FROM user_news WHERE (user_id=?" +
					" AND news_id=?);";
				PreparedStatement query = conn.prepareStatement(queryStr);
				query.setInt(1, node.getUserId());
				query.setString(2, node.getGuid());
				query.executeUpdate();
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
			DataSource ds = DataSourceProvider.getDataSource();
			try (Connection conn = ds.getConnection()) {
				conn.setAutoCommit(false);
				Statement stat = conn.createStatement();
				try {
					String query = "DELETE FROM user_news WHERE news_id IN " +
						"(SELECT guid FROM news  WHERE (DATEDIFF((SELECT NOW()), pub_date) >=" +
						MAX_AGE + "))";
					stat.executeUpdate(query);
					query = "DELETE FROM news WHERE (DATEDIFF((SELECT NOW()), pub_date) >="
						+ MAX_AGE + ")";
					stat.executeUpdate(query);
					conn.commit();
				} catch (SQLException ex){
					conn.rollback();
					logger.log(Level.SEVERE, ex.getMessage(), ex);
				} finally {
					conn.setAutoCommit(true);
				}
			} catch(SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
