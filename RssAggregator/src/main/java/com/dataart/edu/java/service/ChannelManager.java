package com.dataart.edu.java.service;

import com.dataart.edu.java.dao.ChannelDao;
import com.dataart.edu.java.domain.Channel;
import com.dataart.edu.java.domain.NewsNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ChannelManager
{
	private ChannelDao dao;
	private static Logger logger = Logger.getLogger(ChannelDao.class.getName());
	
	public ChannelManager()
	{
		dao = new ChannelDao();
	}
	
	public void save(Channel ch)
	{
		dao.save(ch);
	}
	
	public void delete(Channel ch)
	{
		dao.delete(ch);
	}
	
	public void update(Channel ch)
	{
		try {
			URL address = new URL(ch.getUrl());
			try (InputStream stream = address.openStream()) {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(stream);
				
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				String xpathStr = "/rss/channel/item";
				NodeList result = (NodeList) xpath.evaluate(xpathStr, doc, XPathConstants.NODESET);
				int length = result.getLength();
				NewsNode node;
				for (int j = 0; j < length; ++j)
				{
					xpathStr = "guid";
					String guid = URLEncoder.encode(xpath.evaluate(xpathStr, result.item(j)), "UTF-8");
					xpathStr = "title";
					String title = xpath.evaluate(xpathStr, result.item(j));
					xpathStr = "link";
					String link = xpath.evaluate(xpathStr, result.item(j));
					xpathStr = "description";
					String description = xpath.evaluate(xpathStr, result.item(j));
					xpathStr = "pubDate";
					String pubDate = xpath.evaluate(xpathStr, result.item(j));
					String formattedDate;
					SimpleDateFormat sqlToDate;
					SimpleDateFormat dateToStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						sqlToDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzzzz", Locale.ENGLISH);
						Date date = sqlToDate.parse(pubDate);
						formattedDate = dateToStr.format(date);
					} catch (ParseException ex) {
						try {
							sqlToDate = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzzzz", Locale.ENGLISH);
							Date date = sqlToDate.parse(pubDate);
							formattedDate = dateToStr.format(date);
						} catch (ParseException ex1) {
							formattedDate = "";
						}
					}
					node = new NewsNode();
					node.setUserId(ch.getUserId());
					node.setGuid(guid);
					node.setTitle(title);
					node.setLink(link);
					node.setDescription(description);
					node.setPubDate(formattedDate);
					node.setChannelId(dao.getId(ch));
					NewsNodeManager nnm = new NewsNodeManager();
					nnm.save(node);
				}
			}
		} catch (IOException | ParserConfigurationException |
			SAXException | XPathExpressionException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public List<Channel> getChannels(int userId)
	{
		return dao.getChannels(userId);
	}
}
