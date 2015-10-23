package com.dataart.edu.java.dao;

import com.dataart.edu.java.auxiliary.DataSourceProvider;
import com.dataart.edu.java.domain.Channel;
import java.util.LinkedList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

public class ChannelDao
{
	@Transactional
	public void save(Channel ch)
	{
		SqlRowSet rs = JDBC_TEMPLATE.queryForRowSet(GET_CHANNEL_ID, ch.getUrl());
		if (!rs.next())
			JDBC_TEMPLATE.update(INSERT_CHANNEL, ch.getUrl());
		
		boolean recordExists = JDBC_TEMPLATE.queryForObject(CHECK_RELATION,
			new Object[]{ch.getUserId(), ch.getId()}, Integer.class) > 0;
		if (!recordExists)
		{
			Integer channelId = JDBC_TEMPLATE.queryForObject(GET_CHANNEL_ID,
				new Object[]{ch.getUrl()}, Integer.class);
			JDBC_TEMPLATE.update(INSERT_RELATION,
				ch.getUserId(), channelId, ch.getName());
		}
	}

	@Transactional
	public void delete(Channel ch)
	{
		int uid = ch.getUserId();
		String url = ch.getUrl();
		JDBC_TEMPLATE.update(DELETE_CHANNEL, uid, url);
		JDBC_TEMPLATE.update(DELETE_NEWS, uid, url);
	}

	public int getId(Channel ch)
	{
		int result;
		try {
			result = JDBC_TEMPLATE.queryForObject(
				GET_CHANNEL_ID, new Object[]{ch.getUrl()}, Integer.class);
		} catch (EmptyResultDataAccessException ee) {
			result = 0;
		}
		return result;
	}

	public List<Channel> getChannels(int uid)
	{
		List<Channel> channels = new LinkedList<>();
		SqlRowSet rs = JDBC_TEMPLATE.queryForRowSet(GET_CHANNELS, uid);
		int channelId;
		while (rs.next())
		{
			channelId = rs.getInt("id");
			String name = JDBC_TEMPLATE.queryForObject(
				GET_CHANNEL_NAME, new Object[]{channelId, uid}, String.class);
			Channel ch = (new Channel.Builder()).setId(channelId).setName(name).
				setUrl(rs.getString("url")).setUserId(uid).build();
			channels.add(ch);
		}
		return channels;
	}
	
	private static final JdbcTemplate JDBC_TEMPLATE =
		new JdbcTemplate(DataSourceProvider.getDataSource());

	private static final String GET_CHANNEL_ID = "SELECT id FROM channels WHERE url=?";
	private static final String GET_CHANNEL_NAME =
		"SELECT name FROM user_channel WHERE channel_id=? AND user_id=?";	
	private static final String GET_CHANNELS =
		"SELECT id, url FROM channels where id IN (" +
		"SELECT	channel_id FROM user_channel WHERE user_id=?)";
	private static final String INSERT_CHANNEL = "INSERT INTO channels(url) VALUES(?)";
	private static final String CHECK_RELATION =
		"SELECT COUNT(user_id) FROM user_channel WHERE user_id=? AND channel_id=?";
	private static final String INSERT_RELATION =
		"INSERT INTO user_channel VALUES(?, ?, ?)";
	private static final String DELETE_CHANNEL =
		"DELETE FROM user_channel WHERE user_id=? AND" +
		" channel_id=(SELECT id FROM channels WHERE url=?)";
	private static final String DELETE_NEWS =
		"DELETE FROM user_news WHERE user_id=? AND" +
		" news_id IN (SELECT guid FROM news WHERE" +
		" channel_id=(SELECT id FROM channels WHERE url=?))";
}
