package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.SessionFactory;
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


    @Test(expected = DatabaseConfigurationException.class)
    public void verifyHibernateConfigError() throws DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();

        config.changeConfigFile(11, "1");
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        HibernateUtils.verifyHibernateConfig("hibernate.cfg.xml");

    }

    @Test
    public void getConnection() throws DatabaseConnectionException, DatabaseConfigurationException, SQLException {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile(11, "0");

        Connection connectionn = DatabaseUtils.getConnection();

        assertNotNull(connectionn);
        connectionn.close();
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void getConnectionError() throws DatabaseConnectionException, DatabaseConfigurationException {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile(11, "1");

        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        DatabaseUtils.getConnection();
    }

    @Test
    public void getSessionFactory() throws DatabaseInitializationException {
        SupportMethods config = new SupportMethods();
        config.changeConfigFile(11, "0");

        clearEnvironmentVariable();
        //setEnvironmentVariable(url);

        SessionFactory sessionOne = DatabaseUtils.getSessionFactory();
        SessionFactory sessionTwo = DatabaseUtils.getSessionFactory();

        assertNotNull("Failed to return a new instance of Session", sessionOne);
        assertNotNull("Failed to return a current instance of Session", sessionTwo);

    }

}