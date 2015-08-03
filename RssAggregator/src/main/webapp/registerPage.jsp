<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registration</title>
    </head>
    <body>
        <div style="float: left">
            <form name="loginForm" method="POST" action="register">
                <p>
                    <label>Username</label>
                    <input type="text" name="username" class="data">
                </p>
                <p>
                    <label>Full name</label>
                    <input type="text" name="fullName" class="data">
                </p>
                <p>
                    <label>Password</label>
                    <input type="password" name="password" class="data">
                </p>
                <p>
                    <label>Confirm &nbsp;</label>
                    <input type="password" name="confirm" class="data">
                </p>
                <input type="submit" disabled="true" value="Register" class="btn">
            </form>
        </div>
        <div style="float: left; margin-left: 50px; margin-top: 10px">
            Username must contain alphanumeric characters, underscore and dot.<br>
            Numbers, underscore and dot can't be at the end or start of a username.<br>
            Underscore and dot can't be next to each other.<br>
            Underscore or dot can't be used multiple times in a row (e.g user__name / user..name).<br>
            Number of characters must be between 4 and 20.<br><br>
            
            Full name must contain alphabetic characters and spaces.<br><br>
            
            Password can't contain spaces.<br>
            Number of characters must be between 4 and 20.
        </div>
        <script charset="utf-8" src="js/checkInput.js"></script>
    </body>
</html>
