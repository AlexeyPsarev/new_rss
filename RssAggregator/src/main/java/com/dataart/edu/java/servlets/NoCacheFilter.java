package com.dataart.edu.java.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter("*")
public class NoCacheFilter implements Filter
{
	@Override
	public void init(final FilterConfig filterConfig)
	{
	}
	
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
		throws IOException, ServletException
	{
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setDateHeader("Expires", 0);

        chain.doFilter(req, res);
    }
	
	@Override
	public void destroy()
	{
	}
}
