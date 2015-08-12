package com.dataart.edu.java.domain;

public class NewsNode
{
	private int userId;
	private String guid;
	private String title;
	private String link;
	private String description;
	private String pubDate;
	private boolean read;
	private int channelId;
	
	public NewsNode()
	{
		userId = 0;
		read = false;
		channelId = 0;
	}
	
	public void setUserId(int uid)
	{
		userId = uid;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public void setGuid(String id)
	{
		guid = id;
	}
	
	public String getGuid()
	{
		return guid;
	}
	
	public void setRead(boolean isRead)
	{
		read = isRead;
	}
	
	public boolean isRead()
	{
		return read;
	}
	
	public void setChannelId(int id)
	{
		channelId = id;
	}
	
	public int getChannelId()
	{
		return channelId;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setLink(String link)
	{
		this.link = link;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public void setDescription(String desc)
	{
		description = desc;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setPubDate(String date)
	{
		pubDate = date;
	}
	
	public String getPubDate()
	{
		return pubDate;
	}
}
