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

@WebServlet("/addChannel")
public class AddChannelServlet extends HttpServlet
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
		Channel ch;
		if (request.getContentType() != null &&
			request.getContentType().toLowerCase().contains("multipart/form-data"))
		{
			List<String> names = (List<String>)request.getAttribute("channelNames");
			List<String> urls = (List<String>)request.getAttribute("channelUrls");
			int length = names.size();
			for (int i = 0; i < length; ++i)
			{
				ch = new Channel();
				ch.setUserId(Integer.parseInt(request.getAttribute("userId").toString()));
				ch.setName(names.get(i));
				ch.setUrl(urls.get(i));
				manager.save(ch);
				manager.update(ch);
			}
		}
		else
		{
			ch = new Channel();
			ch.setUserId(Integer.parseInt(request.getParameter("userId")));
			ch.setName(request.getParameter("channelName"));
			ch.setUrl(request.getParameter("channelUrl"));
			manager.save(ch);
			manager.update(ch);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
