package com.dataart.edu.java.servlets;

import com.dataart.edu.java.domain.Channel;
import com.dataart.edu.java.service.ChannelManager;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/updateNews")
public class UpdateNewsServlet extends HttpServlet
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
		int userId = Integer.parseInt(request.getParameter("userId"));
		List<Channel> channels = manager.getChannels(userId);
		for (Channel ch: channels)
			manager.update(ch);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
