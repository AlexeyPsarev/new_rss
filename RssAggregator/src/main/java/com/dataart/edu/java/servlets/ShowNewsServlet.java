package com.dataart.edu.java.servlets;

import com.dataart.edu.java.models.NewsNode;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
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

@WebServlet("/showNews")
public class ShowNewsServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException 
	{
		try {
			request.setCharacterEncoding("UTF-8");
			int userId;
			String keyword;
			String beginDate;
			String endDate;
			String dateSort;
			boolean multipart = request.getContentType() != null &&
				request.getContentType().toLowerCase().contains("multipart/form-data");
			if (multipart)
			{
				userId = Integer.parseInt((String)request.getAttribute("userId"));
				keyword = (String)request.getAttribute("keyword");
				beginDate = (String)request.getAttribute("beginDate");
				endDate = (String)request.getAttribute("endDate");
				dateSort = (String)request.getAttribute("dateSort");
			}
			else
			{
				userId = Integer.parseInt(request.getParameter("userId"));
				keyword = request.getParameter("keyword");
				beginDate = request.getParameter("beginDate");
				endDate = request.getParameter("endDate");
				dateSort = request.getParameter("dateSort");
			}
			int channelId = (int)request.getAttribute("channelId");
			int page;
			try {
				if (multipart)
					page = Integer.parseInt((String)request.getAttribute("pageNum"));
				else
					page = Integer.parseInt(request.getParameter("pageNum"));
			} catch (NumberFormatException ex) {
				page = 1;
			}
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/UsersDB");
			try (Connection conn = ds.getConnection()) {
				Statement stat = conn.createStatement();
				StringBuilder query = new StringBuilder();
				query.append(" FROM news WHERE (guid IN (SELECT news_id " +
					"FROM user_news WHERE (user_id=" + userId + "))");
				if ((keyword != null) && !(keyword.isEmpty()))
				{
					query.append(" AND (title LIKE \"%" + keyword +
						"%\" OR description LIKE \"%" + keyword + "%\")");
				}
				if ((beginDate != null) && !(beginDate.isEmpty()))
					query.append(" AND pub_date>=\"" + beginDate + "\"");
				if ((endDate != null) && !(endDate.isEmpty()))
					query.append(" AND pub_date<=\"" + endDate + "\"");
				if (channelId != 0)
					query.append(" AND channel_id=" + channelId + ")");
				else
					query.append(")");
				
				String countQuery = "SELECT COUNT(*)" + query.toString() + ";";
				ResultSet countSet = stat.executeQuery(countQuery);
				countSet.next();
				int newsCount = countSet.getInt(1);
				int pageCount = (newsCount % 10 == 0) ?
					(newsCount / 10) : (newsCount / 10 + 1);
				
				String selectQuery = "SELECT *" + query.toString() + " ORDER BY pub_date";
				if (!("Asc".equals(dateSort)))
					selectQuery += " DESC";
				int newsBegin = (page - 1) * 10;
				selectQuery += " LIMIT " + newsBegin + ", 10;";
				ResultSet rs = stat.executeQuery(selectQuery);
				List<NewsNode> list = new LinkedList<>();
				while (rs.next())
				{
					String pubDate = rs.getDate("pub_date").toString();
					String pubTime = rs.getTime("pub_date").toString();
					NewsNode node = new NewsNode(
						userId, rs.getString("guid"), rs.getString("title"),
						rs.getString("link"), rs.getString("description"),
						pubDate + " " + pubTime, channelId);
					list.add(node);
				}

				request.setAttribute("news", list);
				request.setAttribute("userId", userId);
				request.setAttribute("pageNum", page);
				request.setAttribute("pageCount", pageCount);
				RequestDispatcher dispatcher = request.getRequestDispatcher("newsPage.jsp");
				dispatcher.forward(request, response);
			} catch (SQLException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
			}
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	private static Logger logger = Logger.getLogger(ShowNewsServlet.class.getName());
}
