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

package com.pkin.stocksearch.servlet;

import com.pkin.stocksearch.model.SearchDAO;
import com.pkin.stocksearch.service.DatabaseService;
import com.pkin.stocksearch.service.exceptions.StockServiceException;
import com.pkin.stocksearch.utilities.WebUtils;
import com.pkin.stocksearch.utilities.exceptions.WebUtilsException;
import com.pkin.stocksearch.service.StockService;
import com.pkin.stocksearch.service.ServiceFactory;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;
import org.apache.commons.lang3.builder.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.sql.Timestamp;

/**
 * Simple servlet to return historical stock quote data using
 * the YahooFinance-API.
 */
@WebServlet("/StockSearch")
public class StockSearchServlet extends HttpServlet {

    private static final String SYMBOL_PARAMETER_KEY = "stockSymbol";
    private static final String START_PARAMETER_KEY = "startDate";
    private static final String END_PARAMETER_KEY = "endDate";
    private static final String INTERVAL_PARAMETER_KEY = "interval";
    private static final String QUICKSYMBOL_PARAMETER_KEY = "quickSymbol";

    private static final String ERROR_HTML = "<tr><td>An error occurred. Please try again.</td>";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        //String to return formatted results to ReturnedResults.jsp
        String FORMATTED_HTML_QUERY ="";

        //Retrieve current active session for local usage
        HttpSession session = request.getSession();

        //Retrieve session data from submission form.
        final String symbol = request.getParameter(SYMBOL_PARAMETER_KEY);
        final String start = request.getParameter(START_PARAMETER_KEY);
        final String end = request.getParameter(END_PARAMETER_KEY);
        final String interval = request.getParameter(INTERVAL_PARAMETER_KEY);
        final String quickSymbol = request.getParameter(QUICKSYMBOL_PARAMETER_KEY);

        //Get an instance of StockService from its' factory method.
        StockService service = ServiceFactory.getStockServiceInstance();
        DatabaseService databaseService = ServiceFactory.getDatabaseServiceInstance();

        //If condition is true, return a quick quote.
        if(quickSymbol != null) {
            int flag = 0;
            Stock stock = null;
            final String INVALID_QUERY = "<tr><td>" + quickSymbol.toUpperCase() + "  is an invalid stock symbol.</td></tr>";

            try {
                stock = YahooFinance.get(quickSymbol);
            } catch (FileNotFoundException e) {
                flag = 1;
            }
            /*
               Switch statement to let the user know they entered an invalid query,
               or build the table by default if FileNotFoundException was not caught.
             */
            switch (flag) {
                case (1): {
                    FORMATTED_HTML_QUERY = INVALID_QUERY;
                    break;
                }
                default: {
                    FORMATTED_HTML_QUERY = WebUtils.buildTable(stock, 1);
                    break;
                }
            }
        }
        //If condition is true, return a historical quote.
        else if(symbol != null){
            Calendar calendarStart;
            Calendar calendarEnd;

            try {
                calendarStart = WebUtils.stringToCalendar(start);
                calendarEnd = WebUtils.stringToCalendar(end);
            } catch (WebUtilsException e) {
                throw new ServletException("Failed to parse raw date string", e.getCause());
            }

            //Parse the chosen interval from raw form data.
            Interval finalInterval = Interval.valueOf(interval);

            Stock intervalResults; //List to hold queried results before parsing to HTML format
            try {
                //Get the goods from Yahoo
                intervalResults = service.getQuote(symbol, calendarStart, calendarEnd, finalInterval);
                //Finalized HTML formatted String to hold dynamic HTML.
                FORMATTED_HTML_QUERY = WebUtils.buildTable(intervalResults, 2);
            } catch (StockServiceException e) {
                FORMATTED_HTML_QUERY = ERROR_HTML;
            }

        }

        /*
         * Super Important!!!
         * Store formatted query in session data to be accessed by results page.
         * Nothing will be done to HTML formatted results after it is cast to final string.
         * All work is done server-side to protect data integrity.
         *
         */
        session.setAttribute("formattedQuote", FORMATTED_HTML_QUERY);

        ServletContext servletContext = session.getServletContext();
        RequestDispatcher dispatcher =
                servletContext.getRequestDispatcher("/ReturnedResults.jsp");
        dispatcher.forward(request, response);

        //Commit search data to DB
        Calendar calendar = Calendar.getInstance();
        Timestamp currentTimestamp = new Timestamp(calendar.getTime().getTime());

        SearchDAO searchDAO = new SearchDAO();

        if (symbol != null) {
            searchDAO.setStockSymbol(symbol.toUpperCase());
            searchDAO.setTypeOfSearch(0);
        } else if (quickSymbol != null) {
            searchDAO.setStockSymbol(quickSymbol.toUpperCase());
            searchDAO.setTypeOfSearch(1);
        }

        searchDAO.setDate(currentTimestamp);
        searchDAO.setStockId(1);
        searchDAO.setSystemId(2);
        searchDAO.setBrowserId(4);
        searchDAO.setUserId(10001);

        databaseService.commitObject(searchDAO);

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
        return "StockSearchServlet{}";
    }
}
