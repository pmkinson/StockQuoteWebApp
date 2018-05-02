package com.uml.edu.stocksearch.service;

import org.junit.Test;

import static com.uml.edu.stocksearch.service.ServiceFactory.getStockServiceInstance;
import static org.junit.Assert.*;

public class FormDataServiceFactoryTest {

    @Test
    public void getStockServiceInstanceTest() {
        StockService service = getStockServiceInstance();
        StockService badService = null;

        assertNotNull("Verify factory returns an instance of StockService", service);
        assertNull("Verify StockService returns null", badService);
    }
}