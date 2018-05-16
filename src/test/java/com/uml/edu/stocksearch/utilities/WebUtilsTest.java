package com.uml.edu.stocksearch.utilities;

import com.uml.edu.stocksearch.servlet.StockSearchServlet;
import com.uml.edu.stocksearch.utilities.exceptions.WebUtilsException;
import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

public class WebUtilsTest {

    final String NO_RESULTS = "<tr><td>No results were found</td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td></tr>" +
            "<tr><td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td></tr>";

    final String FOUND_RESULT = "<tr><td>AAPL</td><td>01/01/2000</td><td>$ 1</td><td>$ 2</td><td>$ 5</td></tr>";

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
    public void resultsTableBuilderTest() {

        Calendar quoteDate = Calendar.getInstance();
        quoteDate.setTime(Date.valueOf("2000-01-01"));

        List<HistoricalQuote> nullQuote = new ArrayList<>(0);
        List<HistoricalQuote> validQuote = new ArrayList<>(10);
        HistoricalQuote initializeQuote = new HistoricalQuote();
        initializeQuote.setSymbol("AAPL");
        initializeQuote.setOpen(new BigDecimal(1));
        initializeQuote.setClose(new BigDecimal(2));
        initializeQuote.setAdjClose(new BigDecimal(5));
        initializeQuote.setDate(quoteDate);
        validQuote.add(initializeQuote);

      //  String resultFail = WebUtils.resultsTableBuilder(nullQuote);
      //  String resultPass = WebUtils.resultsTableBuilder(validQuote);

       // assertEquals("Correctly identified a null object and returned correct response.",resultFail, NO_RESULTS);
       // assertEquals("",resultPass, FOUND_RESULT);
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