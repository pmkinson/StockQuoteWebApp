package com.uml.edu.stocksearch.utilities.database;

import com.uml.edu.stocksearch.utilities.WebUtils;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.uml.edu.stocksearch.utilities.database.DatabaseUtils.getConnection;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.Assert.assertThat;

public class DatabaseUtilsTest {

    private Configuration configuration = new Configuration();
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
        }
    }

    @Test
    public void getHibernateConfigTest() throws DatabaseConfigurationException {

        clearEnvironmentVariable();
        setEnvironmentVariable(url);

        String read = System.getenv("DATABASE_URL");
        configuration = DatabaseUtils.getDBCredentials(configuration);

        Properties properties = new Properties();

        String expUserName = "yyntvlsruodewk";
        String expPassword = "824bc9538b51522e5fe41537b41b611998efc8e7da23422dd5b0edf86370a69a";
        String expUrl = "jdbc:postgresql://ec2-174-129-206-173.compute-1.amazonaws.com:5432/dee5uoi05ai36v";

        String actualUserName = configuration.getProperty("hibernate.connection.username");
        String actualPassword = configuration.getProperty("hibernate.connection.password");
        String actualUrl = configuration.getProperty("hibernate.connection.url");

        assertEquals("Password failed to be updated in the hibernate config file", expPassword, actualPassword);
        assertEquals("Username failed to be updated in the hibernate config file", expUserName, actualUserName);
        assertEquals("URL failed to be updated in the hibernate config file", expUrl, actualUrl);

    }

    @Test(expected = DatabaseConfigurationException.class)
    public void getHibernateConfigError() throws DatabaseConfigurationException {
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        DatabaseUtils.getDBCredentials(configuration);

    }

    @Test
    public void getConnection() throws DatabaseConnectionException, DatabaseConfigurationException {
        clearEnvironmentVariable();
        setEnvironmentVariable(url);

        DatabaseUtils.getConnection();
    }

    @Test
    public void getConnectionError() throws DatabaseConnectionException, DatabaseConfigurationException {
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);

        DatabaseUtils.getConnection();
    }
    @Test
    public void getSessionFactory() {
    }

    @Test
    public void shutdown() {
    }

    @Test
    public void commitDAOObject() {
    }


}