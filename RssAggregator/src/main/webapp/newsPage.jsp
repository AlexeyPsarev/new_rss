<%@page import="java.net.URLDecoder"%>
<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.List"%>
<%@page import="com.dataart.edu.java.models.Channel"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Cache-control" content="NO-CACHE">
        <title>News</title>
        
        <c:choose>
            <c:when test="${param.dateSort != \"Asc\"}">
                <c:set var="orderValue" value="Desc"/>
            </c:when>
            <c:otherwise>
                <c:set var="orderValue" value="Asc"/>
            </c:otherwise>
        </c:choose>

        <style>
            .page {
                border:none;
                padding:0;
                font: inherit;
            }
        </style>
    </head>
    <body>
        <div id="common">
            <input type="hidden" name="userId" value=${userId}>
            <input type="hidden" name="dateSort" value=${orderValue}>
        </div>
        <div style="float: left; width: 800px">
            <div style="text-align: center">
                <input type="radio" value="Desc" name="dateOrder"
                       <c:if test="${orderValue != \"Asc\"}">
                           <c:out value="checked=true"/>
                       </c:if> >
                <label>Descending Date</label>
                <input type="radio" value="Asc" name="dateOrder"
                       <c:if test="${orderValue == \"Asc\"}">
                           <c:out value="checked=true"/>
                       </c:if> >
                <label>Ascending Date</label>
            </div>
            <div style="text-align: center; margin-top: 10px; margin-left: 10px">
                <form action="parseRss" method="POST">
                    <div id="filters">
                        <div style="display: inline-block">
                            <label>Keyword: </label><br>
                            <input type="text" class="filter" name="keyword" value="${param.keyword}">
                        </div>
                        <div style="display: inline-block; margin-left: 5px">
                            <label>After date: </label><br>
                            <input type="text" class="filter" name="beginDate" value="${param.beginDate}">
                        </div>
                        <div style="display: inline-block; margin-left: 5px; margin-right: 5px">
                            <label>Before date: </label><br>
                            <input type="text" class="filter" name="endDate" value="${param.endDate}">
                        </div>
                    </div>
                    <div style="text-align: center; vertical-align: central">
                        <input type="submit" value="Search" onclick="addParameters(this)">
                        <input type="button" value="Cancel" style="margin-top: 5px" onclick="clearFilter()">
                    </div>
                </form>
            </div>
            <div style="margin-top: 10px; margin-left: 10px">
                <c:forEach var="el" items="${news}">
                    <c:choose>
                        <c:when test="${el.read}">
                            <c:set var="fontStyle" value="normal"/>
                            <c:set var="readDisabling" value="disabled"/>
                            <c:set var="unreadDisabling" value=""/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="fontStyle" value="bold"/>
                            <c:set var="readDisabling" value=""/>
                            <c:set var="unreadDisabling" value="disabled"/>
                        </c:otherwise>
                    </c:choose>
                    <div style="font-weight: ${fontStyle}">
                        <p>${el.pubDate}</p>
                        <p><a href="${el.link}" target="_blank" onclick="markNews('${el.guid}', true)">${el.title}</a></p>
                        <p>${el.description}</p>
                    </div>
                    <p>
                        <input type="button" value="Mark as read" onclick="markNews('${el.guid}', true)" ${readDisabling}>
                        <input type="button" value="Mark as unread" onclick="markNews('${el.guid}', false)" ${unreadDisabling}>
                        <input type="button" value="Delete" onclick="delNews('${el.guid}')">
                    </p>
                    <hr>
                </c:forEach>
            </div>
            <div style="margin-bottom: 10px">
                <c:forEach begin="1" end="${pageCount}" var="cur">
                    <c:set var="text" value="${cur}"/>
                    <c:choose>
                        <c:when test="${pageNum == cur}">
                            <c:set var="refStyle" value="background: antiquewhite"/>
                            <c:set var="eventHandler" value=""/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="refStyle" value="border-bottom:1px solid #444; cursor: pointer; background:none;"/>
                            <c:set var="eventHandler" value="onclick='changePage(this)'"/>
                        </c:otherwise>
                    </c:choose>
                    <input type="button" class="page" style="${refStyle}" ${eventHandler} value="${cur}">
                    &nbsp&nbsp
                </c:forEach>
            </div>
        </div>
        <div style="float: left; margin-left: 10px; margin-top: 10px">
            Load RSS channels from file
            <form action="upload" method="post" enctype="multipart/form-data">
                <input name="urlsList" type="file" onchange="uploadFile(this)">
            </form>
            <form action="addChannelPage.jsp" method="POST">
                <p>
                    <input type="submit" value="Add Channel" name="addBtn" onclick="addParameters(this)">
                </p>
            </form>
            <form action="delChannel" method="POST">
                <p>
                    <select name="channelItem" id="channelNames"
                            size=<%= ((List<Channel>)request.getAttribute("channels")).size() + 1 %>>
                        <option value="all" id="0">All</option>
                        <c:forEach var="item" items="${channels}">
                            <option value=${item.url} id="${item.id}">${item.name}</option>
                        </c:forEach>
                    </select>
                </p>
                <p>
                    <input type="submit" value="Delete Channel" name="delBtn" id="delBtn" onclick="addParameters(this)">
                </p>
            </form>
            <form action="updateNews" method="POST">
                <p>
                    <input type="submit" value="Refresh" name="refreshBtn" onclick="addParameters(this)">
                </p>
            </form>
            <form action="logout" method="GET">
                <input type="submit" value="Log out">
            </form>
        </div>
    
    <script charset="utf-8">
        var userId = ${userId},
            channelId = ${channelId},
            orderValue = '${orderValue}',
            keyword,
            beginDate,
            endDate;
    </script>
    <script src="js/newsPageActions.js"></script>
</body>
</html>
