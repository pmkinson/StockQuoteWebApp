package com.uml.edu.stocksearch.servlet;

import com.uml.edu.stocksearch.service.StockService;
import com.uml.edu.stocksearch.service.ServiceFactory;
import com.uml.edu.stocksearch.service.exceptions.StockServiceException;
import com.uml.edu.stocksearch.utilities.WebUtils;
import com.uml.edu.stocksearch.utilities.exceptions.WebUtilsException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
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
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Simple servlet to return historical stock quote data using
 * the YahooFinance-API.
 */
@WebServlet("/StockSearchServlet")
public class StockSearchServlet extends HttpServlet {

    private static final String SYMBOL_PARAMETER_KEY = "stockSymbol";
    private static final String START_PARAMETER_KEY = "startDate";
    private static final String END_PARAMETER_KEY = "endDate";
    private static final String INTERVAL_PARAMETER_KEY = "interval";
    private static final String QUICKSYMBOL_PARAMETER_KEY = "quickSymbol";

    public void doPost(HttpServletRequest request, HttpServletResponse response)
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
        final String ERROR_HTML = "<tr><td>An error occurred. Please try again.</td>" +
                "<td></td>" +
                "<td></td>" +
                "<td></td>" +
                "<td></td></tr>" +
                "<tr><td></td>" +
                "<td></td>" +
                "<td></td>" +
                "<td></td>" +
                "<td></td></tr>";

        //Get an instance of StockService from its' factory method.
        StockService service = ServiceFactory.getStockServiceInstance();

        if(quickSymbol != null) {
            Stock stock = YahooFinance.get(quickSymbol);
            FORMATTED_HTML_QUERY = "<h1> " + stock.getStats().getSymbol() + "</h1>";
        }
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

            Stock results; //List to hold queried results before parsing to HTML format
            try {
                //Get the goods from Yahoo
                results = service.getQuote(symbol, calendarStart, calendarEnd, finalInterval);
                //Finalized HTML formatted String to display in jsp results page.
                FORMATTED_HTML_QUERY = WebUtils.historicalQuoteTable(results);
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
        return "StockSearchServlet{}";
    }
}
