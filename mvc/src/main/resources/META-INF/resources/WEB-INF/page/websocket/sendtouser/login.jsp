<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
<form action="/ws/auth" method="post">
    用户名:<input type="text" name="name" />
    <p>
        密码:<input type="password" name="password" />
    <p>
    <input type="submit" value="submit" />
</form>
</body>
</html>