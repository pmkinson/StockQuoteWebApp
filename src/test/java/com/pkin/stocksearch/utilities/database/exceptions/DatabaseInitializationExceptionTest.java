package com.pkin.stocksearch.utilities.database.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseInitializationExceptionTest {
    @Test(expected = DatabaseInitializationException.class)
    public void exceptionTestObject() throws DatabaseInitializationException {
        Throwable o = new Throwable();
        throw new DatabaseInitializationException("Throw this", o);
    }

}