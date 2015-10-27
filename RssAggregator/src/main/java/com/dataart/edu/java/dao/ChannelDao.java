package com.dataart.edu.java.dao;

import com.dataart.edu.java.auxiliary.DataSourceProvider;
import com.dataart.edu.java.domain.Channel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
			Integer.class, ch.getUserId(), ch.getId()) > 0;
		if (!recordExists)
		{
			Integer channelId = JDBC_TEMPLATE.queryForObject(GET_CHANNEL_ID,
				Integer.class, ch.getUrl());
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
		try {
			return JDBC_TEMPLATE.queryForObject(
				GET_CHANNEL_ID, Integer.class, ch.getUrl());
		} catch (EmptyResultDataAccessException ee) {
			return 0;
		}
	}

	public List<Channel> getChannels(int uid)
	{
		List<Channel> channels = JDBC_TEMPLATE.query(GET_CHANNELS, (ResultSet rs, int rowNum) -> {
			return (new Channel.Builder()).setId(rs.getInt("id")).setName(rs.getString("name")).
				setUrl(rs.getString("url")).setUserId(uid).build();
		}, uid);
		return channels;
	}
	
	private static final JdbcTemplate JDBC_TEMPLATE =
		new JdbcTemplate(DataSourceProvider.getDataSource());

	private static final String GET_CHANNEL_ID = "SELECT id FROM channels WHERE url=?";
	private static final String GET_CHANNELS =
		"SELECT id, url, name FROM channels ch JOIN user_channel uch" +
		" ON uch.channel_id = ch.id WHERE user_id=?";
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
