<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" session="false" %>

<%@page import='com.pkin.stocksearch.utilities.database.FileUtils' %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:set var='stocksList' value='${FileUtils.getCSVFile("stocks.csv")}' scope='page'/>

<script>
    /*
    AutoComplete for stock symbols
     */
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

<div class="row col" id="header-col">
    <a class="navbar-brand" href="/index.jsp"><h3 class="jumbo-brand">StockQuote</h3></a>
    <h6 class="jumbo-phrase">A J2EE web application built to showcase numerous frameworks</h6>
</div>

<nav class="navbar navbar-expand-lg navbar-light">

    <a class="navbar-brand collapse" href="/StockSearch">StockQuote</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-item nav-pad btn btn-rounded btn-info btn-text-white" href="./StockSearch.jsp">New
                    Search</a>
            </li>
        </ul>
        <form name="quickquote" class="nav-item form-inline my-2 my-lg-0" action="StockSearchServlet" method="post">
            <input type="text" name="quickSymbol" class="form-control mr-sm-2 bs-autocomplete" aria-label="Quick Quote"
                   autocomplete="off" spellcheck="false" placeholder="Quick Quote" required>
            <button class="nav-pad btn btn-warning btn-rounded btn-text-white" type="submit" formmethod="get">Search
            </button>
        </form>
    </div>
</nav>
<div class="line"></div>


