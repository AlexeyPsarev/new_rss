package com.dataart.edu.java.servlets;

import com.dataart.edu.java.models.User;
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
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		RequestDispatcher dispatcher;
		User user = new User(request.getParameter("username"),
			request.getParameter("password"), null, null);
		if (user.authenticate())
		{
			request.setAttribute("userId", user.getId());
			request.setAttribute("fullName", user.getFullName());
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
