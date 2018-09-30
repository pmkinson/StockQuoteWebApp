package com.pkin.stocksearch.utilities.database.exceptions;

import org.junit.Test;

public class DatabaseConfigurationExceptionTest {
    @Test(expected = DatabaseConfigurationException.class)
    public void exceptionTest() throws DatabaseConfigurationException {
        throw new DatabaseConfigurationException("Throw this");
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void exceptionTestObject() throws DatabaseConfigurationException {
        Throwable o = new Throwable();
        throw new DatabaseConfigurationException("Throw this", o);
    }
}