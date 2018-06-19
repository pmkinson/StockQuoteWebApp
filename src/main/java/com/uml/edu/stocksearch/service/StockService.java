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

package com.uml.edu.stocksearch.service;

import com.uml.edu.stocksearch.service.exceptions.StockServiceException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class StockService {

    protected StockService() {
        //Hiding constructor.
    }

    /**
     * Get a historical list of stock quotes for the provide symbol
     * This method will return one StockQuoteDAO per interval specified.
     *
     * @param symbol       FormData symbol to get a quote for
     * @param from         Start date
     * @param until        End date
     * @param intervalEnum The interval to return quotes by
     * @return ​a list of HistoricalQuotes One for each day in the range specified.
     * @throws StockServiceException  Thrown when there is a failure to retrieve data from YahooFinance.get() method.
     */
    public Stock getQuote(String symbol, Calendar from, Calendar until, Interval intervalEnum) throws StockServiceException {

        Stock stock; //Local variable to return to calling method.

        //Retrieve current quote with historical summaries from YahooFinance-API
        try {
            stock = YahooFinance.get(symbol, from, until, intervalEnum);
        } catch (IOException e) {
            throw new StockServiceException("An error occured while retrieving a list of quotes" +
                    " for, " + symbol + ", using WebStockService. ", e.getCause());
        }

        return stock;
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

    /**
     * Override inherited toString method.
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "StockService{}";
    }
}
