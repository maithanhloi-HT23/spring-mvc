<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 10/13/2022
  Time: 6:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" import="java.io.*" %>
<html>
<head>
    <title>Studen Error</title>
</head>
<body>
<%
    Exception exp = (Exception) request.getAttribute("javax.servlet.error.exception");
    exp.printStackTrace(new java.io.PrintWriter(out));
%>
</body>
</html>
