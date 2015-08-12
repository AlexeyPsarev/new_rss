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

@WebServlet("/delChannel")
public class DeleteChannelServlet extends HttpServlet
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
		String url = request.getParameter("channelItem");
		int userId = Integer.parseInt(request.getParameter("userId"));
		Channel ch = new Channel();
		ch.setUserId(userId);
		ch.setUrl(url);
		manager.delete(ch);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
