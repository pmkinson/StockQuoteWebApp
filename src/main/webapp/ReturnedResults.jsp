<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- Retrieve quick quote and historical quote query symbol.
Set the title of the page to whichever one is not null. -->
<c:set var="quick" value="${paramValues['quickSymbol']}" scope="page"/>
<c:set var="historical" value="${paramValues['stockSymbol']}" scope="page"/>

<c:choose>
    <c:when test="${quick != null}">
        <c:set var="titleSymbol" value="${param['quickSymbol']}" scope="page"/>
    </c:when>
    <c:when test="${historical != null}">
        <c:set var="titleSymbol" value="${param['stockSymbol']}" scope="page"/>
    </c:when>
</c:choose>

<html>
<head>

    <title>StockQuote: ${titleSymbol}</title>

    <!-- JQuery load-->
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-3.3.1.min.js"></script>
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-ui.min.js"></script>
    <!-- BootStrap JS-->
    <script language="JavaScript" type="text/javascript" src="./resources/bootstrap4/js/bootstrap.min.js"></script>
    <!-- BootStrap CSS-->
    <link rel="stylesheet" href="./resources/bootstrap4/css/bootstrap.min.css">
    <link rel="stylesheet" href="./resources/bootstrap4/css/bootstrap-theme.min.css">
    <!--  JQuery UI CSS -->
    <link rel="stylesheet" href="./resources/jquery/css/jquery-ui.css">
    <!-- Load CSS overrides -->
    <link rel="stylesheet" href="./resources/custom/css/bs-override.css">
    <!-- Favicon --->
    <link rel="icon" href="./resources/images/nav/ticker_brand_24.png">

    <script>
        $(document).ready(function () {
            //Load static elements
            $('.nav-element').load('top_nav.html');
            $('footer').load('footer.html');
        });
    </script>

</head>

<body>
<div class="nav-element bg-light"></div>

<div class="container-fluid">
    <div class="row row-spacer hide">
        <div class="col-3">
            <!--  Banner stuff here -->
        </div>
    </div>
    <div class="row">
        <div class="col-sm-1 hide"></div>
        <div class="col-sm-10">
            <!--Results-->
            ${formattedQuote}

        </div>
        <div class="col-sm-1 hide"></div>
    </div>
</div>

</body>
<footer class="bg-light"></footer>
</html>

