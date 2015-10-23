package com.dataart.edu.java.service;

import com.dataart.edu.java.auxiliary.DateFormatConst;
import com.dataart.edu.java.dao.NewsNodeDao;
import com.dataart.edu.java.domain.NewsNode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NewsNodeManager
{
	public int getPageCount(int userId, int channelId,
		String keyword, String beginDate, String endDate)
	{
		final int NEWS_PER_PAGE = NewsNodeDao.NEWS_PER_PAGE;
		int newsCount = dao.getNewsCount(
			userId, channelId, keyword, beginDate, endDate);
		return (newsCount %  NEWS_PER_PAGE == 0) ?
			(newsCount / NEWS_PER_PAGE) : (newsCount / NEWS_PER_PAGE + 1);
	}
	
	public List<NewsNode> getNews(int userId, int channelId, int pageNum,
		String dateSort, String keyword, String beginDate, String endDate)
	{
		return dao.getNews(userId, channelId, (pageNum - 1) * NewsNodeDao.NEWS_PER_PAGE,
			dateSort, keyword, beginDate, endDate);
	}
	
	public void save(NewsNode node)
	{
		dao.save(node);
	}
	
	public void deleteNews(NewsNode node)
	{
		dao.delete(node);
	}
	
	public void autodelete()
	{
		ZonedDateTime earliestDate =
			ZonedDateTime.now(ZoneId.of(DateFormatConst.TIMEZONE)).minusDays(MAX_AGE);
		String formattedDate =
			earliestDate.format(DateTimeFormatter.ofPattern(DateFormatConst.DB_PATTERN));
		dao.delete(formattedDate);
	}

	public void setReadAttr(NewsNode node)
	{
		dao.update(node);
	}

	private static final int MAX_AGE = 7;
	private final NewsNodeDao dao = new NewsNodeDao();
}
