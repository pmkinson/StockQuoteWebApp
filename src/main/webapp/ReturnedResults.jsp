<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- Retrieve quick quote and historical quote query symbol.
Set the title of the page to whichever one is not null. -->
<c:set var="quick" value="${paramValues['quickSymbol']}" scope="page"/>
<c:set var="historical" value="${paramValues['stockSymbol']}" scope="page"/>
<c:set var="jsonHistory" value="${param['jsonHistory']}" scope="page"/>

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

    <script language="JavaScript" type="text/javascript" src="./resources/highcharts/highcharts.js"></script>

</head>

<body>
<div class="nav-element bg-light">
    <c:import var="nav" url="top_nav.jsp"/>
    ${nav}
</div>

<div class="container-fluid">

    <div id="bs-container"></div>
    <div class="row row-spacer"></div>

    <div class="row">
        <div class="col-sm-1 hide"></div>
        <div class="col-sm-10">
            <!--Results-->
            ${formattedQuote}

        </div>
        <div class="col-sm-1 hide"></div>
    </div>
</div>

<script>
    /*
    Show chart for historical quote
     */
    $(document).ready(function () {

        var data = null;
        data = ${jsonHistory};
        if (data != null) {
            var chart = $('#bs-container');
            chart.slideDown(500);
        }
    });//doc ready
</script>

<script>
    $(document).ready(function () {
            var data = ${jsonHistory};
            Highcharts.chart('bs-container', {
                chart: {
                    zoomType: 'x'
                },
                title: {
                    text: '${titleSymbol}'
                },
                subtitle: {
                    text: document.ontouchstart === undefined ?
                        'Click and drag in the plot area to zoom in' : 'Pinch the chart to zoom in'
                },
                xAxis: {
                    type: 'datetime'
                },
                yAxis: {
                    title: {
                        text: 'Price'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    area: {
                        fillColor: {
                            linearGradient: {
                                x1: 0,
                                y1: 0,
                                x2: 0,
                                y2: 1
                            },
                            stops: [
                                [0, Highcharts.getOptions().colors[0]],
                                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                            ]
                        },
                        marker: {
                            radius: 2
                        },
                        lineWidth: 1,
                        states: {
                            hover: {
                                lineWidth: 1
                            }
                        },
                        threshold: null
                    }
                },

                series: [{
                    type: 'area',
                    name: 'Stock Symbol',
                    data: data
                }]
            });
        }
    );

</script>

<div class="row row-spacer"></div>

</body>
<footer class="bg-light">
    <c:import var="footer" url="footer.html"/>
    ${footer}
</footer>
</html>

