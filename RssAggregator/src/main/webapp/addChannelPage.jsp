<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add RSS Channel</title>
        
        <style>
            div {
                margin-bottom: 10px
            }
        </style>
    </head>
    <body>
        <% request.setCharacterEncoding("utf-8"); %>
        <form action="addChannel" method="POST">
            <div>
                <label>Name</label>
                <input type="text" name="channelName" class="data">
            </div>
            <div>
                <label>URL</label>
                <input type="text" name="channelUrl" class="data">
            </div>
            <input type="hidden" name="userId" value=${param.userId}>
            <input type="hidden" name="dateSort" value=${param.dateSort}>
            <input type="hidden" name="keyword" value="${param.keyword}">
            <input type="hidden" name="beginDate" value="${param.beginDate}">
            <input type="hidden" name="endDate" value="${param.endDate}">
            <input type="submit" value="Add" class="btn" disabled="true">
        </form>
        <script charset="utf-8" src="js/checkInput.js"></script>
    </body>
</html>
