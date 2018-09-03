package com.pkin.stocksearch.utilities.database;

import com.ibatis.db.util.ScriptRunner;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatabaseUtilsTest {


    final private String url = "postgres://yyntvlsruodewk:824bc9538b51522e5fe41537b41b611998efc8e7da23422dd5b0edf86370a69a@ec2-174-129-206-173.compute-1.amazonaws.com:5432/dee5uoi05ai36v";
    final private String badURL = "postgres//yyntvls2dd5b0edf86370a69a@ec2-174-129-206-173.compute-1.amazonaws.com:5432/dee5uoi05ai36v";


    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    public void setEnvironmentVariable(String var) {
        environmentVariables.set("DATABASE_URL", var);
    }

    public void clearEnvironmentVariable() {
        if (environmentVariables != null) {
            environmentVariables.clear("DATABASE_URL");
        } else {
        }
    }

    @Before
    public void setUp() throws SQLException, IOException, DatabaseConfigurationException, DatabaseInitializationException {
        SupportMethods config = new SupportMethods();

        config.runScript("drop_table.sql");
        config.runScript("set_up_test_tables.sql");
        config.runScript("add_search_data.sql");

        config.copyMainFile();
    }

    @After
    public void after() {
        SupportMethods derby = new SupportMethods();

        derby.resetMainHibernateFile();
        derby.shutdownTestDB("TEST_DB");
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void verifyHibernateConfigError() throws DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();

        config.changeConfigFile("backend", "1", "hibernate-test.cfg.xml", "", false);
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);

        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");

    }

    @Test
    public void getConnection() throws DatabaseConnectionException, DatabaseConfigurationException, SQLException {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile("backend", "0", "hibernate.cfg.xml", "", false);

        Connection connection = DatabaseUtils.getConnection("hibernate.cfg.xml");

        assertNotNull(connection);
        connection.close();

        SupportMethods shutDown = new SupportMethods();
        shutDown.shutdownTestDB("TEST_DB");
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void getConnectionError() throws DatabaseConnectionException, DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile("backend", "1", "hibernate-test.cfg.xml", "", false);

        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        DatabaseUtils.getConnection("hibernate-test.cfg.xml");
    }

    @Test
    public void getSessionFactory() throws DatabaseInitializationException {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile("backend", "0", "hibernate-test.cfg.xml", "", false);

        clearEnvironmentVariable();

        SessionFactory sessionOne = DatabaseUtils.getSessionFactory();
        SessionFactory sessionTwo = DatabaseUtils.getSessionFactory();

        assertNotNull("Failed to return a new instance of Session", sessionOne);
        assertNotNull("Failed to return a current instance of Session", sessionTwo);

    }

}