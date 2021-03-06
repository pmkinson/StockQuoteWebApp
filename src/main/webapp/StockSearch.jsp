<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import='com.pkin.stocksearch.service.DatabaseService' %>
<%@page import='com.pkin.stocksearch.utilities.database.FileUtils' %>

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
    <!-- Favicon -->
    <link rel="icon" href="./resources/images/nav/ticker_brand_24.png">

    <script>
        /*
        jQuery Date Picker
         */
        $(document).ready(function () {
            //Datepickers for user selected date range on stock interval search page
            $("#startDate").datepicker({
                changeMonth: true,
                changeYear: true,
                yearRange: '120:+0',
                maxDate: 0
            });
            $("#endDate").datepicker({
                changeMonth: true,
                changeYear: true,
                yearRange: '120:+0',
                maxDate: 0
            });

        });
    </script>

    <script>
        /*
        Validate the starting date precedes the ending date before submission.
         */
        $(document).ready(function () {

            $('#submit-form').click(function (click) {
                var startDate = $('#startDate');
                var endDate = $('#endDate');

                var validateStartDate = new Date(startDate.val());
                var validateEndDate = new Date(endDate.val());

                if (validateStartDate >= validateEndDate) {
                    badDateRange($('#startdate-message'), "Please select a starting date that is before the ending date.");
                    $('#startDate-form').click(function () {
                        $('#startdate-message').slideUp(100, function () {
                            $('#startdate-message').css({
                                "display": "none"
                            });
                        });
                    });

                    click.preventDefault();
                }

            }); //end click
        });//end doc ready

        function badDateRange(element, message) {
            element.html(message);
            element.slideDown(500);
            element.css({
                "display": "block"
            });
        }
    </script>

    <script>
        /*
        Visual effect script for showing top searches on page load.
         */
        $(document).ready(function () {

            var topFive = $('#pop-searches');
            topFive.hide();
            topFive.delay(400).slideDown(400);

        });//doc ready
    </script>

    <script>
        /*
        AutoComplete for stock symbols
         */

        <c:set var='stocksList' value='${FileUtils.getCSVFile("stocks.csv")}' scope='page'/>

        $(document).ready(function () {
            $('.bs-autocomplete').autocomplete({
                source: [${stocksList}],
                minLength: [2]
            }).bind('focusin focusout change', function () {
                var pattern = '^[a-zA-Z]+';
                var fullString = $(this).val();
                var trimmed = fullString.match(pattern);

                $(this).val(trimmed);
            });
        });

    </script>

</head>

<body>
<div class="nav-element bg-light">
    <c:import var="nav" url="top_nav.html"/>
    ${nav}
</div>

<div class="container-fluid">
    <div class="row">
        <div id="pop-searches" class="col-sm">
            <div class="film-strip-container row h-auto justify-content-center ">
                <div class="film-strip-title">Popular Searches:</div>

                <c:set var='topFive'
                       value='${DatabaseService.queryDBForTopSearches("hibernate.cfg.xml", 5, 100, false)}'
                       scope='page'/>

                <!-- Get top searches from db -->
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
        <div class="col-sm bs-hidden"></div>
        <div class="col-sm-4">
            <form name="myform" action="StockSearchServlet" method="post">
                <div class="form-group">
                    <label for="symbols">Stock Symbol:</label>
                    <input type="text" class="form-control bs-autocomplete" autocomplete="off" name="stockSymbol"
                           id="symbols" spellcheck="false" required>
                </div>
                <div id="startDate-form" class="form-group">

                    <label for="startDate">Start Date:</label>
                    <input type="text" class="form-control" name="startDate" id="startDate" autocomplete="off" required
                           onkeydown="return false">
                    <div id="startdate-message" class="tip"></div>
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

                    <button id="submit-form" type="submit" class="btn btn-warning btn-text-white" formmethod="get">
                        <input type="HIDDEN" name="submit" value="true">
                        <span class="">Historical Quote</span>
                    </button>
                </div>
            </form>
        </div>
        <div class="col-sm bs-hidden"></div>
    </div>
</div>

</body>
<footer class="bg-light">
    <c:import var="footer" url="footer.html"/>
    ${footer}
</footer>
</html>



