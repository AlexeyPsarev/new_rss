package com.dataart.edu.java.servlets;

import com.dataart.edu.java.domain.Channel;
import com.dataart.edu.java.service.ChannelManager;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet("/parseRss")
public class ParseRssServlet extends HttpServlet 
{
	private ChannelManager manager;
	
	@Override
	public void init() throws ServletException 
	{
		manager = new ChannelManager();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		int userId;
		String channelIdParam;
		int channelId;
		if (request.getContentType() != null &&
			request.getContentType().toLowerCase().contains("multipart/form-data"))
		{
			userId = Integer.parseInt(request.getAttribute("userId").toString());
			channelIdParam = (String)request.getAttribute("channelId");
		}
		else
		{
			userId = Integer.parseInt(request.getParameter("userId"));
			channelIdParam = request.getParameter("channelId");
			
		}
		channelId = (channelIdParam == null || channelIdParam.equals("all")) ?
				0 :
				Integer.parseInt(channelIdParam);
		List<Channel> channels = manager.getChannels(userId);
		request.setAttribute("channels", channels);
		request.setAttribute("channelId", channelId);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/showNews");
		dispatcher.forward(request, response);
	}
}
