package com.dataart.edu.java.dao;

import com.dataart.edu.java.auxiliary.DataSourceProvider;
import com.dataart.edu.java.domain.NewsNode;
import java.util.LinkedList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

public class NewsNodeDao
{
	public int getNewsCount(int userId, int channelId,
		String keyword, String beginDate, String endDate)
	{
		StringBuilder conditionBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("user_id", userId);
		if ((keyword != null) && !(keyword.isEmpty()))
		{
			conditionBuilder.append(KEYWORD_CLAUSE);
			paramSource.addValue("keyword", "%" + keyword.toLowerCase() + "%");
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
			conditionBuilder.append(CHANNEL_CLAUSE);
			paramSource.addValue("channel_id", channelId);
		}
		Integer newsCount;
		try {
			newsCount = NAMED_JDBC_TEMPLATE.queryForObject(
				COUNT_QUERY_BEGIN + conditionBuilder.toString(), paramSource, Integer.class);
		} catch(DataAccessException ex) {
			newsCount = 0;
		}		
		return newsCount;
	}
	
	public List<NewsNode> getNews(int userId, int channelId, int newsBegin,
		String dateSort, String keyword, String beginDate, String endDate)
	{
		StringBuilder conditionBuilder = new StringBuilder();
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("user_id", userId);
		if ((keyword != null) && !(keyword.isEmpty()))
		{
			conditionBuilder.append(KEYWORD_CLAUSE);
			paramSource.addValue("keyword", "%" + keyword.toLowerCase() + "%");
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
			conditionBuilder.append(CHANNEL_CLAUSE);
			paramSource.addValue("channel_id", channelId);
		}
		conditionBuilder.append(" ORDER BY pub_date");
		if (!("Asc".equals(dateSort)))
			conditionBuilder.append(" DESC");
		conditionBuilder.append(" LIMIT ").append(newsBegin).append(", ").append(NEWS_PER_PAGE);
		List<NewsNode> list = new LinkedList<>();
		try {
			SqlRowSet newsSet = NAMED_JDBC_TEMPLATE.queryForRowSet(
				SELECT_QUERY_BEGIN + conditionBuilder.toString(), paramSource);
			while (newsSet.next())
			{
				String pubDate = newsSet.getDate("pub_date").toString();
				String pubTime = newsSet.getTime("pub_date").toString();
				NewsNode node = (new NewsNode.Builder()).
					setUserId(userId).
					setGuid(newsSet.getString("guid")).
					setTitle(newsSet.getString("title")).
					setLink(newsSet.getString("link")).
					setDescription(newsSet.getString("description")).
					setPubDate(pubDate + " " + pubTime).
					setChannelId(channelId).
					setRead(newsSet.getBoolean("is_read")).build();
				list.add(node);
			}
		} catch(DataAccessException ex) {
			return new LinkedList<>();
		}
		return list;
	}

	@Transactional
	public void save(NewsNode node)
	{
		boolean recordExists = JDBC_TEMPLATE.queryForObject(CHECK_NEWS,
			Integer.class, node.getGuid()) > 0;
		if (!recordExists)
		{
			String pubDate = node.getPubDate();
			JDBC_TEMPLATE.update(INSERT_NEWS, node.getGuid(), node.getTitle(),
				node.getLink(), node.getDescription(), node.getChannelId(), pubDate);
		}
		
		recordExists = JDBC_TEMPLATE.queryForObject(CHECK_RELATION,
			Integer.class, node.getUserId(), node.getGuid()) > 0;
		if (!recordExists)
		{
			JDBC_TEMPLATE.update(INSERT_RELATION,
				node.getUserId(), node.getGuid(), node.isRead());
		}
	}

	public void update(NewsNode node)
	{
		JDBC_TEMPLATE.update(UPDATE_IS_READ,
			node.isRead(), node.getUserId(), node.getGuid());
	}
	
	public void delete(NewsNode node)
	{
		JDBC_TEMPLATE.update(DELETE_NEWS, node.getUserId(), node.getGuid());
	}
	
	@Transactional
	public void delete(String earliestDate)
	{
		JDBC_TEMPLATE.update(AUTODELETE_RELATIONS, earliestDate);
		JDBC_TEMPLATE.update(AUTODELETE_NEWS, earliestDate);
	}
	
	public static final int NEWS_PER_PAGE = 10; // used in NewsNodeManager
	private static final JdbcTemplate JDBC_TEMPLATE =
		new JdbcTemplate(DataSourceProvider.getDataSource());
	private static final NamedParameterJdbcTemplate NAMED_JDBC_TEMPLATE =
			new NamedParameterJdbcTemplate(DataSourceProvider.getDataSource());

	private static final String COUNT_QUERY_BEGIN = "SELECT COUNT(guid) FROM news WHERE" +
		" guid IN (SELECT news_id FROM user_news WHERE user_id=:user_id)";
	private static final String SELECT_QUERY_BEGIN = "SELECT * FROM news JOIN user_news" +
		" ON (guid=news_id) AND  (user_id=:user_id)";
	private static final String KEYWORD_CLAUSE =
		" AND (LOWER(title) LIKE :keyword OR LOWER(description) LIKE :keyword)";
	private static final String CHANNEL_CLAUSE = " AND channel_id=:channel_id";
	private static final String CHECK_NEWS = "SELECT COUNT(guid) FROM news WHERE guid=?";
	private static final String INSERT_NEWS =
		"INSERT INTO news(guid, title, link, description, channel_id, pub_date) " +
		"VALUES(?, ?, ?, ?, ?, ?)";
	private static final String CHECK_RELATION =
		"SELECT COUNT(news_id) FROM user_news WHERE user_id=? AND news_id=?";
	private static final String INSERT_RELATION =
		"INSERT INTO user_news VALUES(?, ?, ?)";
	private static final String UPDATE_IS_READ =
		"UPDATE user_news SET is_read=? WHERE user_id=? AND news_id=?";
	private static final String DELETE_NEWS =
		"DELETE FROM user_news WHERE (user_id=? AND news_id=?)";
	private static final String AUTODELETE_RELATIONS = 
		"DELETE FROM user_news WHERE news_id IN " +
		"(SELECT guid FROM news WHERE pub_date < ?)";
	private static final String AUTODELETE_NEWS =
		"DELETE FROM news WHERE pub_date < ?";
}
