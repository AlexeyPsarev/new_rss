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
		String username = request.getParameter("username");
		User user = (new User.Builder()).
			setUsername(username).
			setPassword(request.getParameter("password")).build();
		if (manager.authenticate(user))
		{
			User authenticatedUser = manager.getUserByName(username);
			request.setAttribute("user", authenticatedUser);
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
