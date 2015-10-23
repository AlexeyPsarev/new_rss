package com.dataart.edu.java.domain;

public class User
{
	private final int id;
	private final String username;
	private final String password;
	private final String fullName;
	
	public static class Builder
	{
		private int id;
		private String username;
		private String password;
		private String fullName;
		
		
		public Builder setId(int id)
		{
			this.id = id;
			return this;
		}
		
		public Builder setUsername(String username)
		{
			this.username = username;
			return this;
		}
		
		public Builder setPassword(String password)
		{
			this.password = password;
			return this;
		}
		
		public Builder setFullName(String fullName)
		{
			this.fullName = fullName;
			return this;
		}
		
		public User build()
		{
			return new User(this);
		}
	}

	private User(Builder builder)
	{
		this.id = builder.id;
		this.username = builder.username;
		this.password = builder.password;
		this.fullName = builder.fullName;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getFullName()
	{
		return fullName;
	}
}
