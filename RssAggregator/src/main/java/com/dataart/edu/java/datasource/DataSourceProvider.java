package com.dataart.edu.java.datasource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceProvider 
{
	private static DataSource ds;
	
	private DataSourceProvider()
	{
	}
	
	public static DataSource getDataSource() throws NamingException
	{
		if (ds == null)
		{
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			ds = (DataSource)envContext.lookup("jdbc/UsersDB");
		}
		return ds;
	}
}
