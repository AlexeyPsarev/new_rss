package com.dataart.edu.java.domain;

public class Channel
{
	private final int id;
	private final int userId;
	private	final String url;
	private final String name;

	public static class Builder
	{
		private int id;
		private int userId;
		private	String url;
		private String name;

		public Builder setId(int id)
		{
			this.id = id;
			return this;
		}
		
		public Builder setUserId(int userId)
		{
			this.userId = userId;
			return this;
		}
		
		public Builder setUrl(String url)
		{
			this.url = url;
			return this;
		}
		
		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}
		
		public Channel build()
		{
			return new Channel(this);
		}
	}
	
	private Channel(Builder builder)
	{
		this.id = builder.id;
		this.userId = builder.userId;
		this.url = builder.url;
		this.name = builder.name;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public String getName()
	{
		return name;
	}	
}
