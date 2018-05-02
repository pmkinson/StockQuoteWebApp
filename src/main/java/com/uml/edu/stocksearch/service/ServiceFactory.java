package com.uml.edu.stocksearch.service;

public class ServiceFactory {
    /**
     * StockService Factory method.
     *
     * @return Concrete instance of StockService
     */
    public static StockService getStockServiceInstance() {
        return new StockService();
    }

    /**
     * DatabaseService Factory method.
     *
     * @return Concrete instance of DatabaseService
     */
    public static DatabaseService getDatabaseServiceInstance() {
        return new DatabaseService();
    }
}
