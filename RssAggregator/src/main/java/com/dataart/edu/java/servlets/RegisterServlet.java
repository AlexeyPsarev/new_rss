package com.dataart.edu.java.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataart.edu.java.models.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		RequestDispatcher dispatcher;
		
		Pattern p;
		Matcher m;
		p = Pattern.compile("^(?=.{4,20}$)(?![_.0-9])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
		m = p.matcher(request.getParameter("username"));
		if (!m.matches())
		{
			request.setAttribute("msg",
				"Forbidden username. Please, try again");
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		p = Pattern.compile("^[a-zA-Z ]*$");
		m = p.matcher(request.getParameter("fullName"));
		if (!m.matches())
		{
			request.setAttribute("msg",
				"Forbidden full name. Please, try again");
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		p = Pattern.compile("^(?=.{4,20}$)([\\S]*)$");
		m = p.matcher(request.getParameter("password"));
		if (!m.matches())
		{
			request.setAttribute("msg",
				"Forbidden password. Please, try again");
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if (!request.getParameter("password").equals(request.getParameter("confirm")))
		{
			request.setAttribute("msg",
				"Password doesn't match the confirmation. Please, try again");
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}		
		User user = new User(
			request.getParameter("username"), request.getParameter("password"),
			request.getParameter("confirm"), request.getParameter("fullName"));
		if (user.canCreate())
		{
			user.create();
			request.setAttribute("userId", user.getId());
			request.setAttribute("fullName", request.getParameter("fullName"));
			dispatcher = request.getRequestDispatcher("helloPage.jsp");
			dispatcher.forward(request, response);
		}
		else
		{
			request.setAttribute("msg",
				"User with such name already exists");
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
		}
	}
}
