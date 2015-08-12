package com.dataart.edu.java.domain;

import org.apache.commons.codec.digest.DigestUtils;

public class User
{
	private int id;
	private String username;
	private String password;
	private String fullName;

	public User()
	{
		id = 0;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setPassword(String password)
	{
		this.password = DigestUtils.shaHex(password);
	}

	public String getPassword()
	{
		return password;
	}
	
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}
	
	public String getFullName()
	{
		return fullName;
	}
}
