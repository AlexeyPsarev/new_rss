package com.dataart.edu.java.service;

import com.dataart.edu.java.dao.UserDao;
import com.dataart.edu.java.domain.User;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;

public class UserManager
{
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
		final Random r = new SecureRandom();
		byte[] bSalt = new byte[32];
		r.nextBytes(bSalt);
		String salt = new String(bSalt);
		String passHash = getHash(user.getPassword(), salt);
		dao.saveUser(user, passHash, salt);
	}
	
	public boolean authenticate(User user)
	{
		String name = user.getUsername();
		String correctPassHash = dao.getPassword(name);
		if (correctPassHash == null)
			return false;
		String salt = dao.getSalt(name);
		String passHash = getHash(user.getPassword(), salt);
		return passHash.equals(correctPassHash);
	}
	
	private String getHash(String pass, String salt)
	{
		String result = DigestUtils.shaHex(pass);
		result = result.concat(salt);
		return DigestUtils.shaHex(result);
	}
	
	private final UserDao dao = new UserDao();
}
