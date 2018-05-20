package com.uml.edu.stocksearch.utilities.database;

import com.sun.jndi.toolkit.url.Uri;
import com.uml.edu.stocksearch.utilities.WebUtils;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
;

public class DatabaseUtilsTest {

    private Configuration configuration = new Configuration();
    final private String url = "postgres://yyntvlsruodewk:824bc9538b51522e5fe41537b41b611998efc8e7da23422dd5b0edf86370a69a@ec2-174-129-206-173.compute-1.amazonaws.com:5432/dee5uoi05ai36v";

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Before
    public void setUp() {
        environmentVariables.set("DATABASE_URL", url);
    }

    @Test
    public void updateHibernateConfigTest() throws URISyntaxException, DatabaseConfigurationException, IOException {
        String read = System.getenv("DATABASE_URL");
        DatabaseUtils.updateHibernateConfig();

        Properties properties = new Properties();

        String path = "./src/main/resources/hibernate.cfg.xml";
        File file = new File(path);
        FileInputStream streamIn = new FileInputStream(path);
        FileOutputStream streamOut = new FileOutputStream(path);

        configuration.setProperty("hibernate.connection.username", "Squeak");


        String expUserName = "";
        String expPassword = "";

        String actualUserName = configuration.getProperty("hibernate.connection.username");
        String actualPassword = configuration.getProperty("hibernate.connection.password");

        assertEquals("Username failed to be updated in the hibernate config file", expPassword, actualPassword);
        assertEquals("Username failed to be updated in the hibernate config file", expUserName, actualUserName);

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