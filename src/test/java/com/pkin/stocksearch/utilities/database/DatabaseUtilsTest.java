package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtilsTest {

    @Mock
    SessionFactory mockSessionFactory;
    @Mock
    Connection mockConnection;
    @Mock
    Metadata mockMetaData;
    @Mock
    ServiceRegistry mockServiceRegistry;
    @Mock
    StandardServiceRegistryBuilder mockStandardServiceRegistryBuilder;



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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        //Start database for testing.
        startDerbyDB();

        mockServiceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        ;
        mockMetaData = new MetadataSources(mockServiceRegistry).getMetadataBuilder().build();
    }

    private void startDerbyDB() {

    }

    @Test(expected = DatabaseConfigurationException.class)
    public void verifyHibernateConfigError() throws DatabaseConfigurationException {
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        HibernateUtils.verifyHibernateConfig();

    }

    @Test
    public void getConnection() throws DatabaseConnectionException, DatabaseConfigurationException, SQLException {

        Connection connectionn = DatabaseUtils.getConnection();

        assertNotNull(connectionn);
        connectionn.close();
    }

    @Test(expected = DatabaseConfigurationException.class)
    public void getConnectionError() throws DatabaseConnectionException, DatabaseConfigurationException {
        clearEnvironmentVariable();
        setEnvironmentVariable(badURL);
        DatabaseUtils.getConnection();
    }

    //THIS NEEDS TO BE MOCKED!!! ACTUALLY CONNECTING RIGHT NOW!!!
    @Test
    public void getSessionFactory() throws DatabaseInitializationException, DatabaseConfigurationException {
        clearEnvironmentVariable();
        setEnvironmentVariable(url);

        SessionFactory sessionOne = DatabaseUtils.getSessionFactory();
        SessionFactory sessionTwo = DatabaseUtils.getSessionFactory();

        assertNotNull("Failed to return a new instance of Session", sessionOne);
        assertNotNull("Failed to return a current instance of Session", sessionTwo);

    }

    @Test
    public void shutdown() {
    }

    @Test
    public void commitDAOObject() {
    }


}