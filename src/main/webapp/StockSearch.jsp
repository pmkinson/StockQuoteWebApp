<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="true" %>

<%@page import='com.pkin.stocksearch.utilities.database.DatabaseUtils' %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="formData" class="com.pkin.stocksearch.servlet.StockSearchServlet" scope="request"/>
<jsp:setProperty name="formData" property="*"/>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>StockQuote App</title>
    <!-- JQuery load-->
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-3.3.1.min.js"></script>
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-ui.min.js"></script>
    <!-- BootStrap JS-->
    <script language="JavaScript" type="text/javascript" src="./resources/bootstrap4/js/bootstrap.min.js"></script>
    <!-- BootStrap CSS-->
    <link rel="stylesheet" href="./resources/bootstrap4/css/bootstrap.min.css">
    <link rel="stylesheet" href="./resources/bootstrap4/css/bootstrap-grid.min.css">
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

            //Datepickers for user selected date range on stock interval search page
            $("#startDate").datepicker();
            $("#endDate").datepicker();
        });
    </script>

</head>

<body>
<div class="nav-element bg-light"></div>

<div class="container-fluid">
    <div class="row">
        <div class="col-sm">
            <div class="film-strip-container row h-auto justify-content-center ">
                <div class="film-strip-title">Popular Searches:</div>

                <c:set var='topFive' value='${DatabaseUtils.queryDBForTopSearches()}' scope='session'/>
                <c:forEach items="${topFive}" var="stock">
                    <div class="film-strip-item symbol">
                        <a href="${pageContext.request.contextPath}/StockSearchServlet?quickSymbol=${stock}">${stock}</a>
                    </div>
                </c:forEach>
            </div><!-- End film-strip-container-->
            <div class="line" id="line-underscore"></div>

        </div>
    </div>
    <div class="row row-spacer">
        <div class="col-sm"></div>
        <div class="col-sm-4">
            <form name="myform" action="StockSearchServlet" method="post">
                <div class="form-group">
                    <label for="symbols">Stock Symbol:</label>
                    <input type="text" class="form-control" name="stockSymbol" id="symbols" required>
                </div>
                <div class="form-group">
                    <label for="startDate">Start Date:</label>
                    <input type="text" class="form-control" name="startDate" id="startDate" autocomplete="off" required
                           onkeydown="return false">
                </div>
                <div class="form-group">
                    <label for="endDate">End Date:</label>
                    <input type="text" class="form-control" name="endDate" id="endDate" autocomplete="off" required
                           onkeydown="return false">
                </div>
                <div class="form-group">
                    <label for="intervalName">Quote Interval:</label>
                    <select name="interval" id="intervalName" class="form-control">
                        <option value="DAILY">Daily</option>
                        <option value="WEEKLY">Weekly</option>
                        <option value="MONTHLY">Monthly</option>
                    </select><br>

                    <button type="submit" class="btn btn-warning btn-text-white" formmethod="get">
                        <input type="HIDDEN" name="submit" value="true">
                        <span class="">Historical Quote</span>
                    </button>
                </div>
            </form>
        </div>
        <div class="col-sm"></div>
    </div>
</div>
</body>
<footer class="bg-light"></footer>
</html>


