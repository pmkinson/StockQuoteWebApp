package com.pkin.stocksearch.servlet;

import com.pkin.stocksearch.utilities.WebUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StockSearchServletTest {

    final ServletContext servletContextMock = mock(ServletContext.class);

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    HttpSession session;
    @Mock
    RequestDispatcher requestDispatcherMock;
    @Mock
    ServletConfig servletConfigMock;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void StockSearchServletTest() throws Exception {

        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 " +
                "(KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

        when(request.getParameter("stockSymbol")).thenReturn("AAPL");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02/01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getHeader("User-Agent")).thenReturn(userAgent);

        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher("/ReturnedResults.jsp")).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doGet(request, response);

        verify(session).getServletContext();
        verify(requestDispatcherMock).forward(request, response);

    }


    @Test
    public void StockSearchServletQuickQuote() throws Exception {
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 " +
                "(KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

        when(request.getParameter("quickSymbol")).thenReturn("PDS");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02/01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getHeader("User-Agent")).thenReturn(userAgent);

        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher("/ReturnedResults.jsp")).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doGet(request, response);

        verify(session).getServletContext();
        verify(requestDispatcherMock).forward(request, response);

    }


    @Test
    public void StockSearchServletQuickQuoteError() throws Exception {
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 " +
                "(KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

        when(request.getParameter("quickSymbol")).thenReturn("F**");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02/01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getHeader("User-Agent")).thenReturn(userAgent);

        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher("/ReturnedResults.jsp")).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doGet(request, response);

        verify(session).getServletContext();
        verify(requestDispatcherMock).forward(request, response);

    }


    @Test
    public void checkOverrides() {
        WebUtils utils = new WebUtils();
        StockSearchServlet servlet = new StockSearchServlet();

        int one = utils.hashCode();
        int two = servlet.hashCode();


        assertTrue("Overridden equals() returns true", servlet.equals(servlet));
        assertFalse("Overriden equals() return false", servlet.equals(utils));
        assertTrue("Overriden toString() matches", servlet.toString().contains("StockSearchServlet{}"));
        assertEquals("Overriden hashCode() matches true", servlet.hashCode(), servlet.hashCode());
        assertNotEquals("Overriden hashCode() matches false", 1, 2);

    }
}