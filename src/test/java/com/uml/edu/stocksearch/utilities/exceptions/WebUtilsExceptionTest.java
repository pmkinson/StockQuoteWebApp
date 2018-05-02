package com.uml.edu.stocksearch.utilities.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

public class WebUtilsExceptionTest {
    @Test(expected = WebUtilsException.class)
    public void exceptionTest() throws WebUtilsException {
        throw new WebUtilsException("Throw this");
    }

    @Test(expected = WebUtilsException.class)
    public void exceptionTestObject() throws WebUtilsException {
        Throwable o = new Throwable();
        throw new WebUtilsException("Throw this", o);
    }

}