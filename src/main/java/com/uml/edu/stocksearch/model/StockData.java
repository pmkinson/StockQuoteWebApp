package com.uml.edu.stocksearch.model;

import java.time.format.DateTimeFormatter;

/**
 * Abstract class to wrap all implementations of various StockQuotes with.
 * Contains commonalities that will be shared by all data models.
 * <p>
 * Ie, The stock symbol and a date format will be standardized for all StockQuoteDAO daoobjects.
 *
 * @author Peter Kinson
 */

public abstract class StockData {


    protected DateTimeFormatter dateTimeFormatter;  //Declare a date formatter.

    public StockData() {

        //Return a format of yyyy-MM-dd
        dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }
}