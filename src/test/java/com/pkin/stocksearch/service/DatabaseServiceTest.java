package com.pkin.stocksearch.service;

import com.pkin.stocksearch.service.exceptions.DatabaseServiceException;
import com.pkin.stocksearch.utilities.database.SupportMethods;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseServiceTest {

    @Before
    public void setUp() {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile("hibernate.connection.url", "jdbc:derby:TEST_DB", "hibernate-test.cfg.xml");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void commitObject() {
    }

    @Test
    public void queryDBForTopSearches() throws DatabaseServiceException, DatabaseInitializationException, DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.runScript("drop_table.sql", "TEST_DB");
        config.runScript("set_up_test_tables.sql", "TEST_DB");
        config.runScript("add_search_data110.sql", "TEST_DB");

        ArrayList<String> list;

        list = DatabaseService.queryDBForTopSearches("hibernate-test.cfg.xml", 5, 100);

        int expResult = 5;
        int actualResult = list.size();

        assertEquals("queryDBForTopSearches returned the incorrect number of results", expResult, actualResult);
    }
}