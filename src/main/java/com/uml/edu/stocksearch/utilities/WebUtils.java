package com.uml.edu.stocksearch.utilities;

import com.uml.edu.stocksearch.utilities.exceptions.WebUtilsException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WebUtils extends HttpServlet {

    /**
     * Utility method to convert a string representation of a date
     * into a Calendar object.  YahooFinance-API uses Calendar objects
     * for their as a parameter for their getQuote() methods. This method
     * bridges the raw user input to that requirement.
     *
     * @param rawDate String value to parse into Calendar object.
     * @return Calendar instance
     * @throws WebUtilsException Will be thrown when a null value is passed
     *                           in with LocalDate, or if Date fails to parse
     *                           the LocalDate arg.
     */
    public static Calendar stringToCalendar(String rawDate) throws WebUtilsException {
        Calendar calendar;
        try {
            //Formatters to convert from UI form format
            DateTimeFormatter rawFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter parsedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(LocalDate.parse(rawDate, rawFormat).toString(), parsedFormat);

            //Parse from String to LocalDate into Date and Date into Calendar.
            Date date = java.sql.Date.valueOf(localDate);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (NullPointerException | DateTimeParseException e) {
            throw new WebUtilsException("The string arg, " + rawDate + ", could not be parsed into a Calendar instance. Lo siento.", e.getCause());
        }

        return calendar;
    }

    /**
     * Utility method to build an HTML table from queried results.  Method will return
     * an HTML formatted error, "No results found", string if list is null.
     *
     * @param rawQueryResults HistoricalQuote list returned from YahooFinance API
     * @return A string representation of queried data formatted in HTML.
     */
    public static String historicalQuoteTable(Stock rawQueryResults) {
        final String toPrint;  //Final variable to return to calling method.
        String toPrintLocal = null;  //Local variable before returning final string
        int flag = 0; //Flag for error

        //Reusable html for various user alerts as needed.
        final String ERROR_TABLE =
                "<td></td>" +
                        "<td></td>" +
                        "<td></td>" +
                        "<td></td></tr>" +
                        "<tr><td></td>" +
                        "<td></td>" +
                        "<td></td>" +
                        "<td></td>" +
                        "<td></td></tr>";

        //Final string holding html for 'no results found'
        final String NO_RESULTS = "<tr><td>No results were found</td>" + ERROR_TABLE;
        //Generic error message for user.
        final String ERROR_MESSAGE = "<tr><td>An error occurred. Please try again.</td>" + ERROR_TABLE;

        //Local stock instance for interacting with returned results from yahoo-api
        Stock stock = rawQueryResults;

        //Local list of HistoricalQuotes
        List<HistoricalQuote> historicalQuote = new ArrayList<>();

        try {
            historicalQuote.addAll(stock.getHistory());
        } catch (IOException e) {
            flag = 1;
        }
        //Tell the user there were no results
        if (historicalQuote.size() < 1 || historicalQuote == null) {
            toPrintLocal = NO_RESULTS;
        } else {
            /* Switch statement with a flag is to handle a logic error where
             * toPrintLocal was being over written by the above if statement.
             */
            switch(flag) {
                case (0):{
                    //Build the results table.
                    toPrintLocal = buildTable(rawQueryResults, historicalQuote);
                    break;
                }
                    case(1): {
                        //There was an IOException. Let's handle this with some dignity, eh?
                        toPrintLocal = ERROR_MESSAGE;
                        break;
                    }
                default:
                    toPrintLocal = ERROR_MESSAGE;
            }
        }

        toPrint = toPrintLocal;

        return toPrint;
    }

    /**
     * Method to build the main body for a historical quote query.
     * The dynamic results are recursively built into the body
     * of an html table.
     *
     * @param rawQueryResults Yahoo-Finance API object, type Stock.
     * @param historicalQuote Yahoo-Finance API List of objects, type HistoricalQuote.
     *
     * @return Returns a string with the fully formed html table.
     */
    private static String buildTable(Stock rawQueryResults, List<HistoricalQuote> historicalQuote) {
        int rowCount = 1; //Counter for appending collapsible child rows

        final String resultsTable;
        //Final string holding html for the results table header.
        final String HISTORICAL_HEADER =
                "<h2>" + rawQueryResults.getName() + "</h2>" +
                        "<table class=\"table table-responsive table-hover\">" +
                        "<thead>" +
                        "<tr><th></th><th>Date</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr>\n" +
                        "</thead>" +
                        "<tbody>";

        //Final string holding html tags to close table.
        final String CLOSING_TAGS = "</tbody></table>";

        //Build the user's results table.
        SimpleDateFormat usDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        //Using builder since there could be a lot of string concatenation
        StringBuilder builder = new StringBuilder();

        //Create header for table
        builder.append(HISTORICAL_HEADER);

        for (HistoricalQuote hq : historicalQuote) {

            //Convert Calendar instance to a formatted date string representation.
            String formatedDate = usDateFormat.format(Date.from(hq.getDate().toInstant()));
            //Convert int to string to append to .row_# for css
            String convertedRowCount = Integer.toString(rowCount);
            //Setup clickable table
            builder.append("<tr class=\"clickable results-table\" data-toggle=\"collapse\" id=\"row_" + convertedRowCount +
                    "\" data-target=\".row_" + convertedRowCount + "\">" +
                    "<td><i class=\"glyphicon glyphicon-minus\"></i></td>");
            builder.append("<td>" + formatedDate + "</td>");
            builder.append("<td>$ " + hq.getOpen().setScale(2, RoundingMode.CEILING) + "</td>");
            builder.append("<td>$ " + hq.getHigh().setScale(2, RoundingMode.CEILING) + "</td>");
            builder.append("<td>$ " + hq.getLow().setScale(2, RoundingMode.CEILING) + "</td>");
            builder.append("<td>$ " + hq.getClose().setScale(2, RoundingMode.CEILING) + "</td>");
            builder.append("<td>" + hq.getVolume() + "</td></tr></th>");

            builder.append("<tr class=\"collapse row_" + convertedRowCount + "\">" +
                    "<th colspan=\"4\" class=\"panel-body\">"
                    +"</th></tr>");

            rowCount++;
        }
        //Append closing tags for table
        builder.append(CLOSING_TAGS);

        resultsTable = builder.toString();

        return resultsTable;
    }

    /**
     * Override inherited equals method.
     *
     * @return boolean
     */

    @Override
    public boolean equals(Object obj)
    {

        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Override inherited hasChode method
     *
     * @return Object hashcode
     */
    @Override
    public int hashCode()
    {

        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "WebUtils{}";
    }
}
