package com.dataart.edu.java.service;

import com.dataart.edu.java.auxiliary.DateFormatConst;
import com.dataart.edu.java.dao.ChannelDao;
import com.dataart.edu.java.domain.Channel;
import com.dataart.edu.java.domain.NewsNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
					String guid = URLEncoder.encode(xpath.evaluate("guid", result.item(j)), "UTF-8");
					String title = xpath.evaluate("title", result.item(j));
					String link = xpath.evaluate("link", result.item(j));
					String description = xpath.evaluate("description", result.item(j));
					String pubDate = xpath.evaluate("pubDate", result.item(j));
					String formattedDate;
					try {
						ZonedDateTime date = ZonedDateTime.parse(pubDate,
							DateTimeFormatter.ofPattern(DateFormatConst.WITH_DAY_NAME, Locale.ENGLISH));
						formattedDate = date.format(DateTimeFormatter.ofPattern(DateFormatConst.DB_PATTERN));
					} catch (DateTimeException ex1) {
						try {
							ZonedDateTime date = ZonedDateTime.parse(pubDate,
								DateTimeFormatter.ofPattern(DateFormatConst.WITHOUT_DAY_NAME, Locale.ENGLISH));
							formattedDate = date.format(DateTimeFormatter.ofPattern(DateFormatConst.DB_PATTERN));
						} catch (DateTimeException ex2) {
							ZonedDateTime date = ZonedDateTime.now(ZoneId.of(DateFormatConst.TIMEZONE));
							formattedDate = date.format(DateTimeFormatter.ofPattern(DateFormatConst.DB_PATTERN));
						}
					}
					node = (new NewsNode.Builder()).setUserId(ch.getUserId()).
						setGuid(guid).setTitle(title).setLink(link).
						setDescription(description).setPubDate(formattedDate).
						setChannelId(dao.getId(ch)).build();
					NewsNodeManager nnm = new NewsNodeManager();
					nnm.save(node);
				}
			}
		} catch (IOException | ParserConfigurationException |
			SAXException | XPathExpressionException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public List<Channel> getChannels(int userId)
	{
		return dao.getChannels(userId);
	}
	
	private final ChannelDao dao = new ChannelDao();
	private static final Logger LOGGER = Logger.getLogger(ChannelDao.class.getName());
}
