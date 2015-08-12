<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sign In</title>
    </head>
    <body>
         <form name="signInForm" method="POST" action="authenticate">
            <p>
                <label>Username</label>
                <input type="text" name="username" class="data">
            </p>
            <p>
                <label>Password</label>
                <input type="password" name="password" class="data">
            </p>
            <input type="submit" value="Enter" class="btn" disabled>
        </form>
        <script charset="utf-8" src="js/checkInput.js"></script>
    </body>
</html>
