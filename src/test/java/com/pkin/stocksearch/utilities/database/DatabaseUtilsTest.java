package com.pkin.stocksearch.utilities.database;

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

import java.sql.Connection;
import java.sql.SQLException;

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
    public void setUp() {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile("hibernate.connection.url", "jdbc:derby:TEST_DB", "hibernate-test.cfg.xml");
    }

    @After
    public void after() {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile("hibernate.connection.url", "jdbc:derby:TEST_DB", "hibernate-test.cfg.xml");
        config.shutdownTestDB("TEST_DB");
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void verifyHibernateConfigError() throws DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.changeConfigFile("backend", "1", "hibernate-test.cfg.xml");
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);

        HibernateUtils.verifyHibernateConfig("hibernate-test.cfg.xml");

    }

    @Test
    public void getConnection() throws DatabaseConnectionException, DatabaseConfigurationException, SQLException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.changeConfigFile("backend", "0", "hibernate-test.cfg.xml");

        Connection connection = DatabaseUtils.getConnection("hibernate-test.cfg.xml", true);

        assertNotNull(connection);
        connection.close();

        config.shutdownTestDB("TEST_DB");
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void getConnectionError() throws DatabaseConnectionException, DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.changeConfigFile("backend", "1", "hibernate-test.cfg.xml");

        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        DatabaseUtils.getConnection("hibernate-test.cfg.xml", true);
    }

    @Test(expected = DatabaseInitializationException.class)
    public void getSessionFactoryError2() throws DatabaseInitializationException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.changeConfigFile("hibernate.connection.url", "0", "hibernate-test.cfg.xml");
        SessionFactory session = DatabaseUtils.getSessionFactory("hibernate-test.cfg.xml", true);

    }

    @Test(expected = DatabaseConnectionException.class)
    public void getConnectionError2() throws DatabaseConnectionException, DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.changeConfigFile("hibernate.connection.url", "0", "hibernate-test.cfg.xml");
        Connection connection = DatabaseUtils.getConnection("hibernate-test.cfg.xml", true);

    }

    @Test(expected = DatabaseInitializationException.class)
    public void getSessionFactoryError() throws DatabaseInitializationException {
        SupportMethods config = new SupportMethods();

        config.createDB("TEST_DB");
        config.changeConfigFile("hibernate.connection.url", "0", "hibernate-test.cfg.xml");
        SessionFactory sessionOne = DatabaseUtils.getSessionFactory("hibernate-test.cfg.xml", true);
    }

    @Test
    public void getSessionFactory() throws DatabaseInitializationException {
        SupportMethods config = new SupportMethods();

        config.changeConfigFile("backend", "0", "hibernate-test.cfg.xml");
        config.createDB("TEST_DB");

        clearEnvironmentVariable();

        SessionFactory sessionOne = DatabaseUtils.getSessionFactory("hibernate-test.cfg.xml", true);
        SessionFactory sessionTwo = DatabaseUtils.getSessionFactory("hibernate-test.cfg.xml", false);

        assertNotNull("Failed to return a new instance of Session", sessionOne);
        assertNotNull("Failed to return a current instance of Session", sessionTwo);


    }

}