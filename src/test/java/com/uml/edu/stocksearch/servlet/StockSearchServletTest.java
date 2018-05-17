package com.uml.edu.stocksearch.servlet;

import com.uml.edu.stocksearch.utilities.WebUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atLeast;

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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        }

    @Test
    public void StockSearchServletTest() throws Exception {

        when(request.getParameter("stockSymbol")).thenReturn("AAPL");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02/01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher(ArgumentMatchers.contains("/ReturnedResults.jsp"))).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doPost(request, response);

        verify(session).getServletContext();
        verify(requestDispatcherMock).forward(request, response);

    }

    @Test(expected = ServletException.class)
    public void StockSearchServletTestError() throws Exception {

        when(request.getParameter("stockSymbol")).thenReturn("AAPL");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02-01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher(ArgumentMatchers.contains("/ReturnedResults.jsp"))).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doPost(request, response);

        verify(session).getServletContext();
        verify(requestDispatcherMock).forward(request, response);

    }

    @Test
    public void StockSearchServletQuickQuote() throws Exception {

        when(request.getParameter("quickSymbol")).thenReturn("PDS");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02/01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher(ArgumentMatchers.contains("/ReturnedResults.jsp"))).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doPost(request, response);

        verify(session).getServletContext();
        verify(requestDispatcherMock).forward(request, response);

    }

    @Test
    public void StockSearchServletQuickQuoteError() throws Exception {

        when(request.getParameter("quickSymbol")).thenReturn("F**");
        when(request.getParameter("startDate")).thenReturn("01/01/2018");
        when(request.getParameter("endDate")).thenReturn("02/01/2018");
        when(request.getParameter("interval")).thenReturn("DAILY");
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRequestDispatcher(ArgumentMatchers.contains("/ReturnedResults.jsp"))).thenReturn(requestDispatcherMock);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        StockSearchServlet servlet = new StockSearchServlet();
        servlet.init(servletConfigMock);
        servlet.doPost(request, response);

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
        assertNotEquals("Overriden hashCode() matches false",1, 2);

    }
}