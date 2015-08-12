package com.dataart.edu.java.servlets;

import com.dataart.edu.java.domain.NewsNode;
import com.dataart.edu.java.service.NewsNodeManager;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/showNews")
public class ShowNewsServlet extends HttpServlet
{
	private NewsNodeManager manager;
	
	@Override
	public void init() throws ServletException 
	{
		manager = new NewsNodeManager();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException 
	{
		request.setCharacterEncoding("UTF-8");
		int userId;
		String keyword;
		String beginDate;
		String endDate;
		String dateSort;
		boolean multipart = request.getContentType() != null &&
			request.getContentType().toLowerCase().contains("multipart/form-data");
		if (multipart)
		{
			userId = Integer.parseInt((String)request.getAttribute("userId"));
			keyword = (String)request.getAttribute("keyword");
			beginDate = (String)request.getAttribute("beginDate");
			endDate = (String)request.getAttribute("endDate");
			dateSort = (String)request.getAttribute("dateSort");
		}
		else
		{
			userId = Integer.parseInt(request.getParameter("userId"));
			keyword = request.getParameter("keyword");
			beginDate = request.getParameter("beginDate");
			endDate = request.getParameter("endDate");
			dateSort = request.getParameter("dateSort");
		}
		int channelId = (int)request.getAttribute("channelId");
		int page;
		try {
			if (multipart)
				page = Integer.parseInt((String)request.getAttribute("pageNum"));
			else
				page = Integer.parseInt(request.getParameter("pageNum"));
		} catch (NumberFormatException ex) {
			page = 1;
		}
		int pageCount = manager.getPageCount(
			userId, channelId, dateSort, keyword, beginDate, endDate);
		List<NewsNode> list = manager.getNews(
			userId, channelId, page, dateSort, keyword, beginDate, endDate);
		request.setAttribute("news", list);
		request.setAttribute("userId", userId);
		request.setAttribute("pageNum", page);
		request.setAttribute("pageCount", pageCount);
		RequestDispatcher dispatcher = request.getRequestDispatcher("newsPage.jsp");
		dispatcher.forward(request, response);
	}
}
