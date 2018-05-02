package com.uml.edu.stocksearch.service.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

public class StockServiceExceptionTest {

    @Test(expected = StockServiceException.class)
    public void exceptionTest() throws StockServiceException {
        throw new StockServiceException("Throw this");
    }

    @Test(expected = StockServiceException.class)
    public void exceptionTestObject() throws StockServiceException {
        Throwable o = new Throwable();
        throw new StockServiceException("Throw this", o);
    }

}