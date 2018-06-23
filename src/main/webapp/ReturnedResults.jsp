<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="true" %>

<!-- Retrieve Stock Symbol from stored the form session data -->
<% String titleSymbol = "";

        if (request.getParameter("stockSymbol") != null) {
            titleSymbol = request.getParameter("stockSymbol").toUpperCase();
        }
        else if (request.getParameter("quickSymbol") != null) {
            titleSymbol = request.getParameter("quickSymbol");
        }
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>

    <title>StockQuote: <%= titleSymbol %></title>

    <!-- JQuery load-->
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-3.3.1.min.js"></script>
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-ui.min.js"></script>
    <!-- BootStrap JS-->
    <script language="JavaScript" type="text/javascript" src="./resources/bootstrap/js/bootstrap.min.js"></script>
    <!-- BootStrap CSS-->
    <link rel="stylesheet" href="./resources/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="./resources/bootstrap/css/bootstrap-theme.min.css">
    <!--  JQuery UI CSS -->
    <link rel="stylesheet" href="./resources/jquery/css/jquery-ui.css">
    <!-- Load CSS overrides -->
    <link rel="stylesheet" href="./resources/custom/css/local.css">
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
<div class="nav-element"></div>

<div class="container-fluid">
        <div class="col-sm-10">
                    <!-- Print the queried results stored as a string in the session
                         by the StockSearchServlet.  Finalized data only, is available
                         to the user.  No business logic is performed on user's machine.-->
                    ${formattedQuote}
        </div>
</div>
</body>
<footer></footer>
</html>

