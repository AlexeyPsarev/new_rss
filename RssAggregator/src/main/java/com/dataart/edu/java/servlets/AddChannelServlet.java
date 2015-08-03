package com.dataart.edu.java.servlets;
import com.dataart.edu.java.models.Channel;

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
				ch = new Channel(
					Integer.parseInt(request.getAttribute("userId").toString()),
					names.get(i), urls.get(i));
				ch.save();
				ch.update();
			}
		}
		else
		{
			ch = new Channel(
				Integer.parseInt(request.getParameter("userId")),
				request.getParameter("channelName"),
				request.getParameter("channelUrl"));
			ch.save();
			ch.update();
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
