package com.dataart.edu.java.servlets;

import com.dataart.edu.java.service.NewsNodeManager;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/autodelete")
public class AutoDeleteServlet extends HttpServlet
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
		manager.autodelete();
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
}
