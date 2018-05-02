<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html>
<head>
    <title>Index</title>
</head>
<body>

<!-- Redirect all root incoming requests to the App -->
<%
    String redirectURL = "StockSearch.jsp";
    response.sendRedirect(redirectURL);
%>
</body>
</html>