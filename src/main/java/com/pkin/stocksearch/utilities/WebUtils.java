/**
 * Copyright 2018 Peter Kinson
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */

package com.pkin.stocksearch.utilities;

import com.pkin.stocksearch.utilities.exceptions.WebUtilsException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.math.BigDecimal;
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

    //Reusable html for various user alerts as needed.
    private final static String ERROR_TABLE = "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td></tr>" +
            "<tr><td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td></tr>";
    //Final string holding html for 'no results found'
    private final static String NO_RESULTS = "<tr><td>No results were found</td></tr>" + ERROR_TABLE;
    //Generic error message for user.
    private final static String ERROR_MESSAGE = "<tr><td>An error occurred. Please try again.</td></tr>" + ERROR_TABLE;
    //Final string holding html tags to close table.
    private final static String CLOSING_TAGS = "</tbody></table>\n</div> <!--End Card-Body-->";
    private final static String BREAK = "<br>";

    /**
     * Utility method to convert a string representation of a date
     * into a Calendar object.  YahooFinance-API uses Calendar objects
     * a parameter type for their getQuote() methods. This method
     * parses the raw user input to that requirement.
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
     * Controller utility method to build dynamic HTML content.
     * Returns an HTML formatted error message by default.
     * <p>
     * tableID:
     * 1 - QuickQuote Table
     * 2 - Historical Table
     * 3 - Available for slot development.
     * 5 - Available for slot development.
     * </p>
     *
     * @param data    Data object to build into HTML table.
     * @param tableID int value corresponding to what table you want built.
     * @return final String representing the dynamic HTML.
     */
    public static String buildTable(Object data, int tableID) {
        String toPrintLocal = null;

        final String toPrint;  //Final variable to return to calling method.

        //Tell the user there were no results, else, build a table
        if (data == null) {
            toPrintLocal = NO_RESULTS;
        } else {

            //This switch statement is the controller. Add new html content methods here.
            switch (tableID) {

                case (1): {
                    toPrintLocal = buildQuickQuoteTable((Stock) data);
                    break;
                }
                case (2): {
                    toPrintLocal = buildHistoricalTable((Stock) data);
                    break;
                }
                case (3): {
                    toPrintLocal = ERROR_MESSAGE;
                    break;
                }
                case (4): {
                    toPrintLocal = ERROR_MESSAGE;
                    break;
                }
                //Something fishy happened, make sure the end user gets an error message.
                default: {
                    toPrintLocal = ERROR_MESSAGE;
                }
            }
        }

        toPrint = toPrintLocal;
        return toPrint;

    }

    /**
     * Utility method to build an HTML table for a quick quote.  Method will return
     * an HTML formatted error, "No results found", string if list is null.
     *
     * @param stock Stock object returned from YahooFinance API
     * @return A string representation of queried data formatted in HTML.
     */
    private static String buildQuickQuoteTable(Stock stock) {
        final String table;
        final String QUOTE_TABLE_HEADER =
                //Header
                "<div class=\"card card-header\">" +
                        "<h2>" + stock.getName() + "</h2>" +
                        "<h5>" + stock.getStockExchange() + ": " +
                        stock.getSymbol() + "</h5> <br>" +
                        "</div> <!--End card header-->" +
                        "<div class=\"card card-body\">" +
                        //Empty table header, setup columns.
                        "<table class=\"table\">" +
                        "<tbody>";

        //Local variables to format for final user output
        BigDecimal currentPrice = stock.getQuote().getPrice();
        BigDecimal open = stock.getQuote().getOpen();
        BigDecimal previousClose = stock.getQuote().getPreviousClose();
        BigDecimal volume = new BigDecimal(stock.getQuote().getAvgVolume());
        BigDecimal dayLow = stock.getQuote().getDayLow();
        BigDecimal dayHigh = stock.getQuote().getDayHigh();
        BigDecimal yearLow = stock.getQuote().getYearLow();
        BigDecimal yearHigh = stock.getQuote().getYearHigh();
        BigDecimal marketCap = stock.getStats().getMarketCap();
        BigDecimal sharesOutstanding = new BigDecimal(stock.getStats().getSharesOutstanding());
        BigDecimal eps = stock.getStats().getEps();

        final String CONTENT =
                "<h3>$" + String.format("%,.2f", currentPrice) + "</h3>" +
                        "<tr><td>Open: $" + String.format("%,.2f", open) + "</td>" +
                        "<td>Previous Close: $" + String.format("%,.2f", previousClose) + "</td>" +
                        "<td>Volume (Average): " + String.format("%,.0f", volume) + "</td></tr>" +

                        "<tr><td>Day's Range: $" + String.format("%,.2f", dayLow) + " - $" +
                        String.format("%,.2f", dayHigh) + "</td>" +
                        "<td>52 Week Range: $" + String.format("%,.2f", yearLow) + " - $" +
                        String.format("%,.2f", yearHigh) + "</td>" +
                        "<td>MarketCap: " + String.format("%,.0f", marketCap) + "</td></tr>" +

                        "<tr><td>Dividend Rate (Yield): " + stock.getDividend().getAnnualYield() + "</td>" +
                        "<td>Shares Outstanding: " + String.format("%,.0f", sharesOutstanding) + "</td>" +
                        "<td>P/E Ratio (EPS): " + String.format("%.2f", eps) + "</td></tr><tr></tr>";

        table = QUOTE_TABLE_HEADER + CONTENT + CLOSING_TAGS;

        return table;
    }


    /**
     * Method to build the main body for a historical quote query.
     * The dynamic results are recursively built into the body
     * of an html table.
     *
     * @param stock Yahoo-Finance API object, type Stock.
     * @return Returns a string with the fully formed html table.
     */
    private static String buildHistoricalTable(Stock stock) {

        //Final string to return content with
        final String table;
        //Local string to handle dynamic content.
        String toPrintLocal = null;
        //Local list of HistoricalQuotes
        List<HistoricalQuote> historicalQuote = new ArrayList<>();
        //Flag for error
        int flag = 0;

        final String STOCK_NAME_HEADER =
                "<div class=\"card card-header\">" +
                        "<h2>" + stock.getName() + "</h2>" +
                        "<h5>" + stock.getStockExchange() + ": " +
                        stock.getSymbol() + "</h5> <br>" +
                        "</div> <!-- End Card-Header-->";

        final String HISTORICAL_TABLE_HEADER =
                STOCK_NAME_HEADER +
                        "<div class=\"card card-body\">" +
                        "<table class=\"table table-hover\">" +
                        "<thead id=\"tableHead\">" +
                        "<tr><th>Date</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr>" +
                        "</thead>" +
                        "<tbody>";

        //Build the user's results table.
        SimpleDateFormat usDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        //Using builder since there could be a lot of string concatenation
        StringBuilder builder = new StringBuilder();

        //Create header for table
        builder.append(HISTORICAL_TABLE_HEADER);

        //Retrieve history from stock arg
        try {
            historicalQuote.addAll(stock.getHistory());
        } catch (IOException e) {
            //Use flag to handle IOException
            flag = 1;
        }
        if (historicalQuote.size() < 1 || historicalQuote == null) {
            //Check for an IOException being thrown with the next switch
            switch (flag) {
                //There was no error, but historicalQuote didn't have any results. Return no results.
                case (0): {
                    toPrintLocal = NO_RESULTS;
                }
                //There was an IOException, return error message.
                case (1): {
                    toPrintLocal = ERROR_MESSAGE;
                    break;
                }
                default: {
                    toPrintLocal = ERROR_MESSAGE;
                    break;
                }
            }
            //Cast finalized table String to error message
            table = toPrintLocal;
        } else {
            for (HistoricalQuote hq : historicalQuote) {

                try {
                    //Convert Calendar instance to a formatted date string representation.
                    String formattedDate = usDateFormat.format(Date.from(hq.getDate().toInstant()));

                    BigDecimal open = handleNull(hq, 4);
                    BigDecimal high = handleNull(hq, 2);
                    BigDecimal low = handleNull(hq, 3);
                    BigDecimal close = handleNull(hq, 1);
                    BigDecimal volume = handleNull(hq, 6);

                    //Build table
                    builder.append("<tr class=\"results-table\" >");
                    builder.append("<td>" + formattedDate + "</td>");

                    builder.append("<td>$ " + String.format("%,.2f", open) + "</td>");
                    builder.append("<td>$ " + String.format("%,.2f", high) + "</td>");
                    builder.append("<td>$ " + String.format("%,.2f", low) + "</td>");
                    builder.append("<td>$ " + String.format("%,.2f", close) + "</td>");
                    builder.append("<td>" + String.format("%,.0f", volume) + "</td></tr></th>");
                } catch (Throwable e) {
                    String nullError = "<tr><td>There was an error retrieving historical data.</td></tr>" + ERROR_TABLE;
                    return nullError;
                }
            }
            //Append closing tags for table
            builder.append(CLOSING_TAGS + BREAK);
            //Cast finalized table String to dynamic html
            table = builder.toString();
        }

        return table;
    }

    /**
     * Nullpointers get thrown when retrieving data from very old dates. This fills in a default value of
     * zero when a null value is found.
     * <p>
     * 1 - Get the closing value.
     * 2 - Get the day high.
     * 3 - Get the day low.
     * 4 - Get the day open.
     * 5 - Get the adjusted close.
     * 6 - Get the day's volume.
     *
     * @param quote  HistoricalQuote to check
     * @param number Data value to check
     * @return
     */
    private static BigDecimal handleNull(HistoricalQuote quote, int number) {
        BigDecimal data;
        try {
            switch (number) {
                case (1): {
                    data = quote.getClose().setScale(2, RoundingMode.CEILING);
                    break;
                }
                case (2): {
                    data = quote.getHigh().setScale(2, RoundingMode.CEILING);
                    break;
                }
                case (3): {
                    data = quote.getLow().setScale(2, RoundingMode.CEILING);
                    break;
                }
                case (4): {
                    data = quote.getOpen().setScale(2, RoundingMode.CEILING);
                    break;
                }
                case (5): {
                    data = quote.getAdjClose().setScale(2, RoundingMode.CEILING);
                    break;
                }
                case (6): {
                    data = new BigDecimal(quote.getVolume());
                    break;
                }
                default: {
                    data = new BigDecimal(0);
                    break;
                }

            }
        } catch (NullPointerException e) {
            data = new BigDecimal(0);
        }
        return data;
    }

    /**
     * Method to turn stock data points into a JSON table
     * for historical chart.
     *
     * @param stock Yahoo stock object
     * @return JSON format string
     * @throws IOException
     */
    public static String jsonChartData(Stock stock) throws IOException {
        StringBuilder jsonString = new StringBuilder();
        List<HistoricalQuote> stockHistory = stock.getHistory();

        int totalStocks = stockHistory.size();
        int counter = 0;

        //Setup Json
        jsonString.append("[");
        for (HistoricalQuote record : stockHistory) {
            Long date = record.getDate().getTimeInMillis();
            BigDecimal close = record.getClose();

            if (counter <= (totalStocks - 2)) {
                jsonString.append("[" + date + ", " + close + "], ");
            } else if (counter <= totalStocks) {
                jsonString.append("[" + date + ", " + close + "]");
            }
            counter++;
        }
        //Close Json
        jsonString.append("]");

        return jsonString.toString();
    }

    /**
     * Override inherited equals method.
     *
     * @return boolean
     */

    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Override inherited hasChode method
     *
     * @return Object hashcode
     */
    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "WebUtils{}";
    }
}
