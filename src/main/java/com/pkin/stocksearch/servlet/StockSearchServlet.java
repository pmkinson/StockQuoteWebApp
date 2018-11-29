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

import ua_parser.Client;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Timestamp;

/**
 * Simple servlet to return historical stock quote data using
 * the YahooFinance-API.
 */
@WebServlet("/StockSearch")
public class StockSearchServlet extends HttpServlet {

    private static final String ERROR_HTML = "<tr><td>An error occurred. Please try again.</td>";
    private Stock intervalResults; //Hold historical data for queried stock

    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException, ServletException {

        /*
        ArrayList to hold session parameters strings

        0 - symbol
        1 - start
        2 - end
        3 - interval
        4 - quickSymbol
        5 - userAgent
        */
        ArrayList<String> sessionParameters = getSessionParameters(httpRequest);

        //Retrieve current active session for local usage
        HttpSession session = httpRequest.getSession();
        //Reset intervalResults
        clearInterval();
        //Build a stockquote from session parameters
        String FORMATTED_HTML_QUERY = buildQuote(sessionParameters);

        String JSON_CHART_DATA = buildJsonChartData();

        /*
         * Store formatted query in session data to be accessed by results page.
         * Nothing will be done to HTML formatted results after it is cast to final string.
         * All work is done server-side to protect data integrity.
         *
         */
        session.setAttribute("formattedQuote", FORMATTED_HTML_QUERY);
        session.setAttribute("jsonHistory", JSON_CHART_DATA);

        ServletContext servletContext = session.getServletContext();
        RequestDispatcher dispatcher =
                servletContext.getRequestDispatcher("/ReturnedResults.jsp");
        dispatcher.forward(httpRequest, httpResponse);

        //Commit client information about search to database
        commitSearchData(sessionParameters);
    }

    /**
     * Build a json list of historical prices
     *
     * @return
     */
    private String buildJsonChartData() {
        String JSON_CHART_DATA = "";
        //Build json list for historical chart
        try {
            if (intervalResults != null) {
                JSON_CHART_DATA = WebUtils.jsonChartData(intervalResults);
            } else if (intervalResults == null) {
                JSON_CHART_DATA = "null";
            }
        } catch (IOException e) {
            JSON_CHART_DATA = "null";
        }

        return JSON_CHART_DATA;
    }

    /**
     * Clear intervalResults
     */
    private void clearInterval() {
        intervalResults = null;
    }

    /**
     * Method to build an array of session data
     * <p>
     * 0 - symbol
     * 1 - start
     * 2 - end
     * 3 - interval
     * 4 - quickSymbol
     * 5 - userAgent
     *
     * @param request
     * @return An ArrayList of strings containing session parameters.
     */
    private ArrayList<String> getSessionParameters(HttpServletRequest request) {
        ArrayList<String> sessionParameters = new ArrayList<>();

        final String symbol = request.getParameter(SessionDataEnum.SYMBOL_PARAMETER_KEY.getValue());
        final String start = request.getParameter(SessionDataEnum.START_PARAMETER_KEY.getValue());
        final String end = request.getParameter(SessionDataEnum.END_PARAMETER_KEY.getValue());
        final String interval = request.getParameter(SessionDataEnum.INTERVAL_PARAMETER_KEY.getValue());
        final String quickSymbol = request.getParameter(SessionDataEnum.QUICKSYMBOL_PARAMETER_KEY.getValue());
        final String userAgent = request.getHeader(SessionDataEnum.USER_AGENT.getValue());

        sessionParameters.add(symbol);
        sessionParameters.add(start);
        sessionParameters.add(end);
        sessionParameters.add(interval);
        sessionParameters.add(quickSymbol);
        sessionParameters.add(userAgent);

        return sessionParameters;
    }

    /**
     * Method to commit search data to be stored in the database.
     *
     * @param sessionParameters
     */
    private void commitSearchData(ArrayList<String> sessionParameters) {
        Calendar calendar = Calendar.getInstance();
        Timestamp currentTimestamp = new Timestamp(calendar.getTime().getTime());
        DatabaseService databaseService = ServiceFactory.getDatabaseServiceInstance();

        Client client = null;
        SearchDAO searchDAO = new SearchDAO();

        //Check symbol for null
        if (sessionParameters.get(0) != null) {
            searchDAO.setStockSymbol(sessionParameters.get(0).toUpperCase());
            searchDAO.setTypeOfSearch(0);
            //Check quickSymbol for  null
        } else if (sessionParameters.get(4) != null) {
            searchDAO.setStockSymbol(sessionParameters.get(4).toUpperCase());
            searchDAO.setTypeOfSearch(1);
        }


        //Parse UserAgent string
        String userAgent = sessionParameters.get(5);
        try {
            client = WebUtils.getClientData(userAgent);

        } catch (WebUtilsException e) {
            //Default information in case of error
            searchDAO.setTimeStamp(currentTimestamp);
            searchDAO.setDevice("Unknown");
            searchDAO.setFamily("Unknown");
            searchDAO.setFamilyVersion("Unknown");
            searchDAO.setOs("Unknown");
            searchDAO.setOsVersion("Unknown");
            searchDAO.setUserId(0);
        }

        //Build DAO object
        searchDAO.setTimeStamp(currentTimestamp);
        searchDAO.setDevice(client.device.family);
        searchDAO.setFamily(client.userAgent.family);
        searchDAO.setFamilyVersion(client.userAgent.major + "_" + client.userAgent.minor +
                "_" + client.userAgent.patch);
        searchDAO.setOs(client.os.family);
        searchDAO.setOsVersion(client.os.major + "_" + client.os.minor +
                "_" + client.os.patch);
        searchDAO.setUserId(0);

        //Commit Object
        databaseService.commitObject(searchDAO, "hibernate.cfg.xml");
    }

    /**
     * Build an HTML formatted quote to return. Builds quote based off from
     * session parameters 'symbol' or 'quicksymbol'.
     *
     * @param sessionParameters
     * @return HTML String
     * @throws IOException
     */
    private String buildQuote(ArrayList<String> sessionParameters) throws IOException {
        //String to return results with
        String FORMATTED_HTML_QUERY = "";

        //Stock object
        Stock stock = null;

        //If quicksymbol isn't null, return a quick quote.
        if (sessionParameters.get(4) != null) {
            int flag = 1;

            try {
                stock = YahooFinance.get(sessionParameters.get(4));
                if (!stock.isValid()) {
                    flag = 0;
                }
            } catch (FileNotFoundException e) {
                flag = 0;
            }

            FORMATTED_HTML_QUERY = checkFlag(flag, stock, sessionParameters);
        }
        //If symbol isn't null, return a historical quote.
        else if (sessionParameters.get(0) != null) {
            try {
                //Get the goods from Yahoo
                setIntervalResults(sessionParameters);
                //Finalized HTML formatted String to hold dynamic HTML.
                FORMATTED_HTML_QUERY = WebUtils.buildTable(intervalResults, 2);

            } catch (StockServiceException | WebUtilsException e) {
                FORMATTED_HTML_QUERY = ERROR_HTML;
            }

        }

        return FORMATTED_HTML_QUERY;
    }

    /**
     * Method to set Stock results for a class scope variable.
     *
     * @param sessionParameters
     * @throws WebUtilsException
     * @throws StockServiceException
     */
    private void setIntervalResults(ArrayList<String> sessionParameters) throws WebUtilsException, StockServiceException {

        //Start
        Calendar calendarStart = WebUtils.stringToCalendar(sessionParameters.get(1));
        //End
        Calendar calendarEnd = WebUtils.stringToCalendar(sessionParameters.get(2));

        //Parse the chosen interval from raw form data.
        Interval finalInterval = Interval.valueOf(sessionParameters.get(3));

        //Get service
        StockService stockService = ServiceFactory.getStockServiceInstance();

        //Get the goods from Yahoo
        intervalResults = stockService.getQuote(sessionParameters.get(0), calendarStart, calendarEnd, finalInterval);

    }

    /**
     * Method to check a flag from buildQuote(ArrayList<String> sessionParameters) method
     * and return the correct quote to buildQuote.
     *
     * @param flag              Flag from buildQuote
     * @param stock             Stock object to build quote from
     * @param sessionParameters
     * @return String with HTML formatted quote.
     */
    private String checkFlag(int flag, Stock stock, ArrayList<String> sessionParameters) {
        String FORMATTED_HTML_QUERY;
        final String INVALID_QUERY = "<tr><td>" + sessionParameters.get(4).toUpperCase() + "  is an invalid stock symbol.</td></tr>";

        /*
               Switch statement to let the user know they entered an invalid query,
               or build the table by default if FileNotFoundException was not caught.
        */
        switch (flag) {
            case (0): {
                FORMATTED_HTML_QUERY = INVALID_QUERY;
                break;
            }
            case (1): {
                FORMATTED_HTML_QUERY = WebUtils.buildTable(stock, 1);
                break;
            }
            default: {
                FORMATTED_HTML_QUERY = WebUtils.buildTable(stock, 1);
                break;
            }
        }

        return FORMATTED_HTML_QUERY;
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
