package com.pkin.stocksearch.utilities.database.exceptions;

import org.junit.Test;

public class DatabaseConnectionExceptionTest {
    @Test(expected = DatabaseConnectionException.class)
    public void exceptionTest() throws DatabaseConnectionException {
        throw new DatabaseConnectionException("Throw this");
    }

    @Test(expected = DatabaseConnectionException.class)
    public void exceptionTestObject() throws DatabaseConnectionException {
        Throwable o = new Throwable();
        throw new DatabaseConnectionException("Throw this", o);
    }

}