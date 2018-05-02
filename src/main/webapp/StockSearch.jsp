<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="true" %>
<jsp:useBean id="formData" class="com.uml.edu.stocksearch.servlet.StockSearchServlet" scope="request"/>
<jsp:setProperty name="formData" property="*"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Simple StockQuote Web App</title>

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
            <li><p id="brand-name">StockQuote</p>
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
<div class="container-fluid">

    <div class="row">
        <div class="col-sm-3"></div>
        <div class="col-sm-6">
            <div class="col-sm-6">
                <form name="myform" action="StockSearchServlet" method="post">
                    <div class="form-group">
                        <label for="symbols">Stock Symbol:</label>
                        <input type="text" class="form-control" name="stockSymbol" id="symbols" required>
                    </div>
                    <div class="form-group">
                        <label for="startDate">Start Date:</label>
                        <input type="text" class="form-control" name="startDate" id="startDate" required
                               onkeydown="return false">
                    </div>
                    <div class="form-group">
                        <label for="endDate">End Date:</label>
                        <input type="text" class="form-control" name="endDate" id="endDate" required
                               onkeydown="return false">
                    </div>
                    <div class="form-group">
                        <label for="intervalName">Quote Interval:</label>
                        <select name="interval" id="intervalName" class="form-control">
                            <option value="DAILY">Daily</option>
                            <option value="WEEKLY">Weekly</option>
                            <option value="MONTHLY">Monthly</option>
                        </select><br>

                        <button type="submit" class="btn btn-info">
                            <input type="HIDDEN" name="submit" value="true">
                            <span class="glyphicon glyphicon-search"></span> Search
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <br>
        <br>
        <br>
        <br>
    </div>
</div>
</body>
<footer>
    <div class="bar"></div>
    <div class="footer-copyright">
        SomeWebsite.com - ©2018
        <div class="bar"></div>
    </div>
    <div class="bar"></div>
</footer>
</html>

