package com.dataart.edu.java.servlets;

import com.dataart.edu.java.domain.User;
import com.dataart.edu.java.service.UserManager;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/authenticate")
public class AuthenticateServlet extends HttpServlet
{
	private UserManager manager;

	@Override
	public void init() throws ServletException 
	{
		manager = new UserManager();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		RequestDispatcher dispatcher;
		User user = new User();
		user.setUsername(request.getParameter("username"));
		user.setPassword(request.getParameter("password"));
		if (manager.authenticate(user))
		{
			request.setAttribute("user", user);
			dispatcher = request.getRequestDispatcher("helloPage.jsp");
			dispatcher.forward(request, response);
		}
		else
		{
			dispatcher = request.getRequestDispatcher("authFailed.jsp");
			dispatcher.forward(request, response);
		}
	}
}
