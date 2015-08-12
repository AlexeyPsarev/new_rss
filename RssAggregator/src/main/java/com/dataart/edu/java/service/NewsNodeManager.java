package com.dataart.edu.java.service;

import com.dataart.edu.java.dao.NewsNodeDao;
import com.dataart.edu.java.domain.NewsNode;
import java.util.List;

public class NewsNodeManager
{
	private NewsNodeDao dao;
	
	public NewsNodeManager()
	{
		dao = new NewsNodeDao();
	}
	
	public int getPageCount(int userId, int channelId,
		String dateSort, String keyword, String beginDate, String endDate)
	{
		final int NEWS_PER_PAGE = dao.NEWS_PER_PAGE;
		int newsCount = dao.getNewsCount(
			userId, channelId, dateSort, keyword, beginDate, endDate);
		int pageCount = (newsCount %  NEWS_PER_PAGE == 0) ?
			(newsCount / NEWS_PER_PAGE) : (newsCount / NEWS_PER_PAGE + 1);
		return pageCount;
	}
	
	public List<NewsNode> getNews(int userId, int channelId, int pageNum,
		String dateSort, String keyword, String beginDate, String endDate)
	{
		return dao.getNews(userId, channelId, (pageNum - 1) * dao.NEWS_PER_PAGE,
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
		dao.delete();
	}
		
	public void setReadAttr(NewsNode node)
	{
		dao.update(node);
	}
}
