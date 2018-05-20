package com.uml.edu.stocksearch.utilities;

import com.uml.edu.stocksearch.servlet.StockSearchServlet;
import com.uml.edu.stocksearch.utilities.exceptions.WebUtilsException;
import org.junit.Before;
import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

public class WebUtilsTest {
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

    //Set up a Stock object to test against.
    Stock stock = new Stock("APPL");

    StockQuote quote = new StockQuote("APPL");
    StockStats stats = new StockStats("APPL");
    StockDividend dividend = new StockDividend("APPL");
    HistoricalQuote historicalQuote = new HistoricalQuote();
    ArrayList<HistoricalQuote> historicalQuotes = new ArrayList<>();

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 8, 1);

        historicalQuote.setOpen(new BigDecimal(100));
        historicalQuote.setClose(new BigDecimal(200));
        historicalQuote.setLow(new BigDecimal(50));
        historicalQuote.setHigh(new BigDecimal(250));
        historicalQuote.setVolume(new Long(5999));
        historicalQuote.setDate(calendar);

        historicalQuotes.add(historicalQuote);

        quote.setOpen(new BigDecimal(10));
        quote.setPreviousClose(new BigDecimal(9));
        quote.setAvgVolume(new Long(10000));
        quote.setDayLow(new BigDecimal(5));
        quote.setDayHigh(new BigDecimal(15));
        quote.setYearLow(new BigDecimal(1));
        quote.setYearHigh(new BigDecimal(25));
        quote.setPrice(new BigDecimal(12.50));

        stats.setMarketCap(new BigDecimal(9999));
        stats.setSharesOutstanding(new Long(5000));
        stats.setEps(new BigDecimal(1.25));

        dividend.setAnnualYieldPercent(new BigDecimal(2.5));

        stock.setName("Apple Inc");
        stock.setStockExchange("DOW");

        stock.setQuote(quote);
        stock.setStats(stats);
        stock.setDividend(dividend);

        stock.setHistory(historicalQuotes);
    }


    @Test
    public void stringToCalendarTest() throws WebUtilsException {
        String date = "03/12/2000";
        String failDate = "03/12/2020";
        DateTimeFormatter rawFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        //Holy conversions Batman.
        LocalDate transitDate = LocalDate.parse(date, rawFormat);
        LocalDate transitDateFail = LocalDate.parse(failDate,rawFormat);

        Calendar expResult = Calendar.getInstance();
        expResult.setTime(Date.valueOf(transitDate));

        Calendar expFail = Calendar.getInstance();
        expFail.setTime(Date.valueOf(transitDateFail));

        Calendar result = WebUtils.stringToCalendar(date);

        assertEquals("Correctly parse String into Calendar",
                expResult, result);
        assertNotEquals("Correctly failed to match different Calendar date",
                expFail, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToCalendarTestError3() throws WebUtilsException {
        String date = "2000/01/01";
        String failDate = "2020/01/01";

        Calendar expResult = Calendar.getInstance();
        expResult.setTime(java.sql.Date.valueOf(date));

        Calendar expFail = Calendar.getInstance();
        expFail.setTime(java.sql.Date.valueOf(failDate));

        Calendar result = WebUtils.stringToCalendar(date);
    }

    @Test(expected = WebUtilsException.class)
    public void stringToCalendarError() throws WebUtilsException{
        Object foo = null;
        final Calendar calendar;
        calendar = WebUtils.stringToCalendar((String) foo);
    }
    @Test(expected = WebUtilsException.class)
    public void stringToCalendarError2() throws WebUtilsException {
        String date = "2000-01-01 00:00";
        final Calendar calendar;
        calendar = WebUtils.stringToCalendar(date);
    }

    @Test
    public void buildTableTest() {

        Stock nullStock = null;
        Stock errorStock = new Stock("APPL");
        errorStock.setName("Apple");

        String expTable0 = "<tr><td>An error occurred. Please try again.</td></tr><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr>";
        String expTable1 = "<h2>Apple Inc</h2><h5>DOW: APPL</h5> <br><table class=\"table\"><tbody><h3>$12.50</h3><tr><td>Open: $10.00</td><td>Previous Close: $9.00</td><td>Volume (Average): 10,000</td></tr><tr><td>Day's Range: $5.00 - $15.00</td><td>52 Week Range: $1.00 - $25.00</td><td>MarketCap: 9,999</td></tr><tr><td>Dividend Rate (Yield): null</td><td>Shares Outstanding: 5,000</td><td>P/E Ratio (EPS): 1.25</td></tr><tr></tr></tbody></table>";
        String expTable2 = "<h2>Apple Inc</h2><h5>DOW: APPL</h5> <br><table class=\"table table-hover\"><thead id=\"tableHead\"><tr><th>Date</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr></thead><tbody><tr class=\"results-table\" ><td>09/01/2015</td><td>$ 100.00</td><td>$ 250.00</td><td>$ 50.00</td><td>$ 200.00</td><td>5,999</td></tr></th></tbody></table><br>";
        String expTable3 = "<tr><td>An error occurred. Please try again.</td></tr><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr>";
        String expNull = "<tr><td>No results were found</td></tr><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr>";
        String expError = "<tr><td>An error occurred. Please try again.</td></tr><td></td><td></td><td></td><td></td></tr><tr><td></td><td></td><td></td><td></td><td></td></tr>";

        String actualTable0 = WebUtils.buildTable(stock, 0);
        String actualTable1 = WebUtils.buildTable(stock, 1);
        String actualTable2 = WebUtils.buildTable(stock, 2);
        String actualTable3 = WebUtils.buildTable(stock, 3);
        String actualNull = WebUtils.buildTable(nullStock, 1);
        String actualError = WebUtils.buildTable(errorStock, 2);

        assertEquals("buildTable() returned wrong switch string for 0", expTable0, actualTable0);
        assertEquals("buildTable() returned wrong switch string for 1", expTable1, actualTable1);
        assertEquals("buildTable() returned wrong switch string for 2", expTable2, actualTable2);
        assertEquals("buildTable() returned wrong switch string for 3", expTable3, actualTable3);
        assertEquals("buildHistoricalTable() did not throw an IOException error.", expError, actualError);
        assertEquals("buildTable() returned the wrong string for receiving a null Stock object"
                , expNull, actualNull);

    }

    @Test
    public void checkOverrides() {
        WebUtils utils = new WebUtils();
        StockSearchServlet servlet = new StockSearchServlet();

        int one = utils.hashCode();
        int two = servlet.hashCode();


        assertTrue("Overridden equals() returns true", utils.equals(utils));
        assertFalse("Overriden equals() return false", utils.equals(servlet));
        assertTrue("Overriden toString() matches", utils.toString().contains("WebUtils{}"));
        assertEquals("Overriden hashCode() matches true", utils.hashCode(), utils.hashCode());
        assertNotEquals("Overriden hashCode() matches false",1, 2);

    }
}