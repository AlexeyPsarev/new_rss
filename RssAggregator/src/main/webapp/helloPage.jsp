<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8" language="java"  pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Hello</title>
    </head>
    <body>
        <form action="autodelete" method="POST">
            <p>
                <c:out value="Hello, ${fullName}"/>
            </p>
            <input type="hidden" value=${userId} name="userId">
            <input type="submit" value="Continue">
        </form>
    </body>
</html>
