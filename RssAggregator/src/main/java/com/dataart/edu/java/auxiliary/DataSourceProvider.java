package com.dataart.edu.java.auxiliary;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceProvider 
{
	private static volatile DataSource ds;
	
	private DataSourceProvider()
	{
	}
	
	public static DataSource getDataSource()
	{
		if (ds == null)
		{
			synchronized (DataSourceProvider.class)
			{
				if (ds == null)
				{
					try {
						Context initContext = new InitialContext();
						Context envContext  = (Context)initContext.lookup("java:/comp/env");
						ds = (DataSource)envContext.lookup("jdbc/UsersDB");
					} catch (NamingException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return ds;
	}
}
