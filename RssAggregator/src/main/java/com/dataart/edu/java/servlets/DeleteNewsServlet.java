package com.dataart.edu.java.servlets;

import com.dataart.edu.java.domain.NewsNode;
import com.dataart.edu.java.service.NewsNodeManager;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/deleteNews")
public class DeleteNewsServlet extends HttpServlet
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
		int userId = Integer.parseInt(request.getParameter("userId"));
		String guid = URLEncoder.encode(request.getParameter("newsId"), "UTF-8");
		NewsNode node = new NewsNode();
		node.setUserId(userId);
		node.setGuid(guid);
		manager.deleteNews(node);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
