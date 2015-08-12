package com.dataart.edu.java.domain;

public class Channel
{
	private int id;
	private int userId;
	private	String url;
	private String name;

	public Channel()
	{
		id = 0;
		userId = 0;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}	
}
