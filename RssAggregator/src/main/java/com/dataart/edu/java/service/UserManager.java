package com.dataart.edu.java.service;

import com.dataart.edu.java.dao.UserDao;
import com.dataart.edu.java.domain.User;

public class UserManager
{
	private UserDao dao;
	
	public UserManager()
	{
		dao = new UserDao();
	}
	
	public User getUserById(int id)
	{
		return dao.getUser(id);
	}
	
	public User getUserByName(String name)
	{
		return dao.getUser(name);
	}
	
	public boolean canCreate(User user)
	{
		return (dao.getUser(user.getUsername()) == null);
	}
	
	public void create(User user)
	{
		dao.saveUser(user);
	}
	
	public boolean authenticate(User user)
	{
		User etalonUser = dao.getUser(user.getUsername(), user.getPassword());
		if (etalonUser != null)
		{
			user.setId(etalonUser.getId());
			user.setFullName(etalonUser.getFullName());
			return true;
		}
		return false;
	}
}
