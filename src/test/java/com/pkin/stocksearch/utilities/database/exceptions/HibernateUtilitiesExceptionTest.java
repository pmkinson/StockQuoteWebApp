package com.pkin.stocksearch.utilities.database.exceptions;

import org.junit.Test;

import static org.junit.Assert.*;

public class HibernateUtilitiesExceptionTest {
    @Test(expected = HibernateUtilitiesException.class)
    public void exceptionTestObjectTwoArgs() throws HibernateUtilitiesException {
        Throwable o = new Throwable();
        throw new HibernateUtilitiesException("Throw this", o);
    }

    @Test(expected = HibernateUtilitiesException.class)
    public void exceptionTestObjectOneArg() throws HibernateUtilitiesException {
        Throwable o = new Throwable();
        throw new HibernateUtilitiesException("Throw this");
    }

}