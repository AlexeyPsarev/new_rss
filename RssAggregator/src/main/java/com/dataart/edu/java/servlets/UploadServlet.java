package com.dataart.edu.java.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		InputStream fileStream = null;
		List<Part> c = (List<Part>)request.getParts();
		Iterator<Part> it = c.iterator();
		InputStream partStream;
		while (it.hasNext())
		{
			Part cur = it.next();
			if (cur.getName().equals("urlsList"))
				fileStream = cur.getInputStream();
			else
			{
				partStream = cur.getInputStream();
				BufferedReader partReader = new BufferedReader(
					new InputStreamReader(partStream, "UTF-8"));
				request.setAttribute(cur.getName(), partReader.readLine());
			}
		}
		
		BufferedReader fileReader = new BufferedReader(
			new InputStreamReader(fileStream, "UTF-8"));
		String name = fileReader.readLine();
		String link = fileReader.readLine();
		List<String> names = new ArrayList<>();
		List<String> links = new ArrayList<>();
		while ((name != null) && !(name.isEmpty()) &&
			   (link != null) && !(link.isEmpty()))
		{
			names.add(name);
			links.add(link);
			name = fileReader.readLine();
			link = fileReader.readLine();
		}
		request.setAttribute("channelNames", names);
		request.setAttribute("channelUrls", links);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/addChannel");
		dispatcher.forward(request, response);
	}
}
