package com.dataart.edu.java.servlets;

import com.dataart.edu.java.models.Channel;
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
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		String url = request.getParameter("channelItem");
		int userId = Integer.parseInt(request.getParameter("userId"));
		Channel ch = new Channel(userId, null, url);
		ch.delete();
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
