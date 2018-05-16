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

<html>

<head  meta charset="UTF-8">
    <title>StockQuote: <%= titleSymbol %></title>
    <!-- Using JQuery UI's lovely datepicker-->
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-1.12.4.js"></script>
    <script language="JavaScript" type="text/javascript" src="./resources/jquery/js/jquery-ui.js"></script>
    <script language="JavaScript" type="text/javascript" src="./resources/custom/js/datepicker.js"></script>
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

</head>

<body>
<div class="navbar">
    <div class="bar" id="header-bar"></div>
    <div class="container-fluid nav-background">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <a class="navbar-brand navbar-brand-icon" href="#">
            <div class="circle">
                <img id="navbar-img" src="./resources/images/nav/ticker_brand_64.png">
            </div>
        </a>
        <ul class="nav navbar-nav">
            <li><p id="brand-name">Stock Quote</p>
        </ul>
        <ul class="nav navbar-nav">
            <li><a class="navbar-fontcolor navbar-link" href="index.jsp">NEW SEARCH</a></li>
        </ul>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse panel-collapse" id="navbar-panel">

            <form name="quickquote" class="navbar-form navbar-right" action="StockSearchServlet" method="post">
                <div class="form-group">
                    <input type="text" name="quickSymbol" class="form-control search-box" placeholder="Quick Quote"
                           required>
                    <button type="submit" class="btn-link glyphicon glyphicon-search search-icon">
                        <input type="HIDDEN" name="submit" value="true">
                    </button>
                </div>
            </form>

        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
    <div class="container navbar-panel">
        <div class="navbar-login"><a>Login</a> or <a>Sign-Up</a></div>
    </div>
    <br>
</div>

        <div class="col-sm-1"></div>
        <div class="col-sm-10">
                    <!-- Print the queried results stored as a string in the session
                         by the StockSearchServlet.  Finalized data only, is available
                         to the user.  No business logic is performed on user's machine.-->
                    ${formattedQuote}
        </div>

</body>

<footer>
    <div class="bar"></div>
    <div class="footer-copyright">
        <span id="footer-logo">SomeWebsite.com - ©2018</span>
        <div class="copyrightBar bar"></div>
    </div>
</footer>

<!-- Toggle +/- glyphs for each parent row -->
<script type="text/javascript">
    $(document).ready(function () {
        $('.results-table').click(function () {
            $(this).closest('.clickable').find('.glyphicon').toggleClass("glyphicon-plus").toggleClass("glyphicon-minus");
        });
    });
</script>
</html>
