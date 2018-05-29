package com.uml.edu.stocksearch.service.exceptions;

import org.junit.Test;

public class DatabaseServiceExceptionTest {

    @Test(expected = DatabaseServiceException.class)
    public void exceptionTest() throws DatabaseServiceException {
        throw new DatabaseServiceException("Throw this");
    }

    @Test(expected = DatabaseServiceException.class)
    public void exceptionTestObject() throws DatabaseServiceException {
        Throwable o = new Throwable();
        throw new DatabaseServiceException("Throw this", o);
    }

}