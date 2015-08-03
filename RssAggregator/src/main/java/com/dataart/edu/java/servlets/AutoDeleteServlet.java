package com.dataart.edu.java.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/autodelete")
public class AutoDeleteServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		try {
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				conn.setAutoCommit(false);
				Statement stat = conn.createStatement();
				try {
					String query = "DELETE FROM user_news WHERE news_id IN " +
						"(SELECT guid FROM news  WHERE (DATEDIFF((SELECT NOW()), pub_date) >= 7))";
					stat.executeUpdate(query);
					query = "DELETE FROM news WHERE (DATEDIFF((SELECT NOW()), pub_date) >= 7)";
					stat.executeUpdate(query);
					conn.commit();
				} catch (SQLException ex){
					conn.rollback();
					logger.log(Level.SEVERE, ex.getMessage(), ex);
				} finally {
					conn.setAutoCommit(true);
				}
			} catch(SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/parseRss");
		dispatcher.forward(request, response);
	}
	
	private static Logger logger = Logger.getLogger(AutoDeleteServlet.class.getName());
}
