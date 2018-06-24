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
<div class="nav-element"></div>

<div class="container-fluid">
    <div class="row row-spacer">
        <div class="row">
        </div>
    </div>
    <div class="row">
        <div class="col-sm"></div>
        <div class="col-sm-10">
            <!-- Print the queried results stored as a string in the session
                 by the StockSearchServlet.  Finalized data only, is available
                 to the user.  No business logic is performed on user's machine.-->
            ${formattedQuote}
        </div>
    </div>
</div>

</body>
<footer class="bg-light"></footer>
</html>

