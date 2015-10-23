package com.dataart.edu.java.domain;

public class NewsNode
{
	private final int userId;
	private final String guid;
	private final String title;
	private final String link;
	private final String description;
	private final String pubDate;
	private final boolean read;
	private final int channelId;
	
	public static class Builder
	{
		private int userId;
		private String guid;
		private String title;
		private String link;
		private String description;
		private String pubDate;
		private boolean read;
		private int channelId;
		
		public Builder setUserId(int uid)
		{
			userId = uid;
			return this;
		}
		
		public Builder setGuid(String id)
		{
			guid = id;
			return this;
		}
		
		public Builder setRead(boolean isRead)
		{
			read = isRead;
			return this;
		}
		
		public Builder setChannelId(int id)
		{
			channelId = id;
			return this;
		}
		
		public Builder setTitle(String title)
		{
			this.title = title;
			return this;
		}
		
		public Builder setLink(String link)
		{
			this.link = link;
			return this;
		}
		
		public Builder setDescription(String desc)
		{
			description = desc;
			return this;
		}
		
		public Builder setPubDate(String date)
		{
			pubDate = date;
			return this;
		}
		
		public NewsNode build()
		{
			return new NewsNode(this);
		}
	}
	
	private NewsNode(Builder builder)
	{
		this.userId = builder.userId;
		this.guid = builder.guid;
		this.title = builder.title;
		this.link = builder.link;
		this.description = builder.description;
		this.pubDate = builder.pubDate;
		this.read = builder.read;
		this.channelId = builder.channelId;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public String getGuid()
	{
		return guid;
	}
	
	public boolean isRead()
	{
		return read;
	}
	
	public int getChannelId()
	{
		return channelId;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getPubDate()
	{
		return pubDate;
	}
}
