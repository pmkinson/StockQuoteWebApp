package com.uml.edu.stocksearch.service;

import org.junit.Test;

import static com.uml.edu.stocksearch.service.ServiceFactory.getDatabaseServiceInstance;
import static com.uml.edu.stocksearch.service.ServiceFactory.getStockServiceInstance;
import static org.junit.Assert.*;

public class ServiceFactoryTest {

    @Test
    public void getStockServiceInstanceTest() {
        StockService service = getStockServiceInstance();
        StockService badService = null;

        assertNotNull("Verify factory returns an instance of StockService", service);
        assertNull("Verify StockService returns null", badService);
    }

    @Test
    public void getDatabaseServiceInstanceTest() {
        DatabaseService service = getDatabaseServiceInstance();
        DatabaseService badService = null;

        assertNotNull("Verify factory returns an instance of DatabaseService", service);
        assertNull("Verify DatabaseService returns null", badService);
    }
}