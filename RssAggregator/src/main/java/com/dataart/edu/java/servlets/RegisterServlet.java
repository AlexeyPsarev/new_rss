package com.dataart.edu.java.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dataart.edu.java.domain.User;
import com.dataart.edu.java.service.UserManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet
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
		
		Pattern p;
		Matcher m;
		p = Pattern.compile(USERNAME_PATTERN);
		m = p.matcher(request.getParameter("username"));
		if (!m.matches())
		{
			request.setAttribute("msg", BAD_USERNAME);
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		p = Pattern.compile(FULLNAME_PATTERN);
		m = p.matcher(request.getParameter("fullName"));
		if (!m.matches())
		{
			request.setAttribute("msg", BAD_FULLNAME);
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		p = Pattern.compile(PASSWORD_PATTERN);
		m = p.matcher(request.getParameter("password"));
		if (!m.matches())
		{
			request.setAttribute("msg", BAD_PASSWORD);
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		if (!request.getParameter("password").equals(request.getParameter("confirm")))
		{
			request.setAttribute("msg", CONFIRMATION_ERROR);
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
			return;
		}		
		User user = (new User.Builder()).
			setUsername(request.getParameter("username")).
			setPassword(request.getParameter("password")).
			setFullName(request.getParameter("fullName")).build();
		if (manager.canCreate(user))
		{
			manager.create(user);
			user = manager.getUserByName(user.getUsername()); // set new ID
			request.setAttribute("user", user);
			dispatcher = request.getRequestDispatcher("helloPage.jsp");
			dispatcher.forward(request, response);
		}
		else
		{
			request.setAttribute("msg", CANNOT_CREATE);
			dispatcher = request.getRequestDispatcher("cannotRegisterPage.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	private static final String USERNAME_PATTERN =
		"^(?=.{4,20}$)(?![_.0-9])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
	private static final String PASSWORD_PATTERN = "^(?=.{8,}$)([\\S]*)$";
	private static final String FULLNAME_PATTERN = "^[a-zA-Z ]*$";
	private static final String BAD_USERNAME =
		"Forbidden username. Please, try again";
	private static final String BAD_FULLNAME =
		"Forbidden full name. Please, try again";
	private static final String BAD_PASSWORD =
		"Forbidden password. Please, try again";
	private static final String CONFIRMATION_ERROR =
		"Password doesn't match the confirmation. Please, try again";
	private static final String CANNOT_CREATE =
		"User with such name already exists";
}
