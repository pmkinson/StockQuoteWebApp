package com.pkin.stocksearch.service;

import com.pkin.stocksearch.service.exceptions.StockServiceException;
import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class StockServiceTest {

    @Test
    public void getQuoteHistorical() throws StockServiceException, ParseException {
        Interval interval = Interval.WEEKLY;
        String symbol = "GOOG";

        Date fromD = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
        Date untilD = new SimpleDateFormat("yyyy-MM-dd").parse("2005-01-01");
        Calendar from = Calendar.getInstance();
        Calendar until = Calendar.getInstance();
        until.setTime(untilD);
        from.setTime(fromD);

        StockService service = getStockService();
        service.getQuote(symbol, from, until, interval);
    }

    @Test(expected = StockServiceException.class)
    public void getQuoteHistoricalErrorsMore() throws StockServiceException, ParseException {
        Interval interval = Interval.WEEKLY;
        String symbol = "*1";

        Date fromD = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
        Date untilD = new SimpleDateFormat("yyyy-MM-dd").parse("2005-01-01");
        Calendar from = Calendar.getInstance();
        Calendar until = Calendar.getInstance();
        until.setTime(untilD);
        from.setTime(fromD);

        StockService service = getStockService();
        service.getQuote(symbol, from, until, interval);
    }

    @Test(expected = StockServiceException.class)
    public void getQuoteHistoricalErrors() throws StockServiceException, ParseException {

        Interval interval = Interval.WEEKLY;
        String symbol = "GOOG";

        Date fromD = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
        Date untilD = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2000-01-01 01:02:03");
        Calendar from = Calendar.getInstance();
        Calendar until = Calendar.getInstance();
        until.setTime(untilD);
        from.setTime(fromD);

        StockService service = getStockService();
        service.getQuote(symbol, from, until, interval);
    }

    @Test
    public void checkOverrides() {
        StockService service = new StockService();
        Stock notTheSameObject = new Stock("APP");

        String expToString = "StockService{}";
        boolean bool = service.equals(notTheSameObject);
        assertTrue("Overridden equals() returned true", service.equals(service));
        assertFalse("Overridden equals() returned false", service.equals(notTheSameObject));
        assertTrue("Overridden toString() returned true", service.toString().contains(expToString));
        assertEquals("Overridden hashCode() returned true", service.hashCode(), service.hashCode());
        assertThat("Overridden hashCode() returned false", service.hashCode(), not(notTheSameObject.hashCode()));
    }

    public static StockService getStockService() {
        return new StockService();
    }

}