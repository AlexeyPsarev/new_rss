package com.dataart.edu.java.servlets;

import com.dataart.edu.java.models.Channel;
import com.dataart.edu.java.models.User;
import java.io.IOException;
import java.util.Iterator;
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
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		int userId = Integer.parseInt(request.getParameter("userId"));
		User user = new User(userId);
		List<Channel> channels = user.getChannels();
		Iterator<Channel> i = channels.iterator();
		while (i.hasNext())
		{
			i.next().update();
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
