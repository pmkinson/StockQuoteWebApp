/**
 * Copyright 2018 Peter Kinson
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */

package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.service.exceptions.DatabaseServiceException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;

import com.pkin.stocksearch.utilities.database.exceptions.HibernateUtilitiesException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;

import com.ibatis.common.jdbc.ScriptRunner;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;

/**
 * A class that contains database related utility methods.
 */

public class DatabaseUtils {

    private static Configuration configuration;
    private static SessionFactory sessionFactory;

    //For hibernate config file
    private static final String HIBERNATE = "hibernate.cfg.xml";

    /**
     * Utility method to return a connection to the database
     *
     * @return <CODE>Connection</CODE> connection
     * @throws DatabaseConnectionException    Thrown when a connection cannot be established to DB
     * @throws DatabaseConfigurationException Thrown when the hibernate configuration file cannot be loaded.
     */
    public static Connection getConnection() throws DatabaseConnectionException, DatabaseConfigurationException {

        HibernateUtils.verifyHibernateConfig(HIBERNATE);
        Connection connection = null;
        Configuration configuration = getConfiguration(HIBERNATE);
        try {

            Class.forName(HibernateUtils.getDriver());

            String databaseUrl = configuration.getProperty("hibernate.connection.url");
            String username = configuration.getProperty("hibernate.connection.username");
            String password = configuration.getProperty("hibernate.connection.password");

            connection = DriverManager.getConnection(databaseUrl, username, password);

        } catch (ClassNotFoundException | SQLException | HibernateUtilitiesException e) {
            throw new DatabaseConnectionException("Could not open a connection to database." + e.getMessage(), e);
        }
        return connection;
    }


    /**
     * A Session Factory
     *
     * @return SessionFactory for use with database transactions
     * @throws DatabaseInitializationException Thrown when there is an issue returning a concrete session
     */
    synchronized public static SessionFactory getSessionFactory() throws DatabaseInitializationException {

        // singleton pattern
        try {
            if (sessionFactory == null) {
                HibernateUtils.verifyHibernateConfig(HIBERNATE);
                sessionFactory = buildSessionFactory();
            } else {
                return sessionFactory;
            }
        } catch (Throwable e) {
            throw new DatabaseInitializationException("Could not return a concrete session factory because of: "
                    + e.getMessage(), e);
        }
        return sessionFactory;
    }


    /**
     * A SessionFactory instance generator.
     *
     * @return <CODE>SessionFactory</CODE> sessionFactory
     */

    private static SessionFactory buildSessionFactory() {

        try {

            // Create the ServiceRegistry from hibernate.cfg.xml
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure(HIBERNATE).build();

            // Create a metadata sources using the specified service registry.
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            return metadata.getSessionFactoryBuilder().build();

        } catch (ExceptionInInitializerError e) {
            throw new ExceptionInInitializerError("SessionFactory creation failed. " + e.getMessage());
        }
    }


    /**
     * Create a new or return an existing database configuration object.
     *
     * @return A hibernate configuration
     * @throws DatabaseConfigurationException
     */
    private static Configuration getConfiguration(String filePath) throws DatabaseConfigurationException {

        try {
            File file = HibernateUtils.getFile(filePath);

            if (configuration == null) {
                configuration = new Configuration();
                configuration.configure(file);
                configuration.getProperties();
            }

        } catch (URISyntaxException e) {
            throw new DatabaseConfigurationException("Failed to load the URI for " + filePath
                    + " configuration file from the classpath.", e);
        } catch (Throwable e) {
            throw new DatabaseConfigurationException("Couldn't load hibernate.cfg.cml configuration file because of: "
                    + e.getMessage(), e);
        }

        return configuration;
    }



    /**
     * Method to retrieve the top searches from the database.
     *
     * @return String with top 5 searches formatted in HTML
     * @throws DatabaseServiceException
     */
    public static ArrayList<String> queryDBForTopSearches() throws DatabaseServiceException {
        ArrayList<String> resultArray = new ArrayList<>();

        ResultSet results = null;
        Connection connection = null;
        try {
            connection = DatabaseUtils.getConnection();
            PreparedStatement statement;
            /*Returns top 5 searches
              This should be done in hibernate.
              Use the ORM for what it was intended to do.
              SQL doesn't always transfer.
             */
            final String query =
                    "SELECT stock_symbol FROM stockquote.stocks\n" +
                            "GROUP BY 1\n" +
                            "ORDER BY count(*) DESC\n" +
                            "LIMIT 5";
            statement = connection.prepareStatement(query);
            results = statement.executeQuery();

            while (results.next()) {
                resultArray.add(results.getString("stock_symbol"));
            }

            connection.close();

        } catch (DatabaseConfigurationException | DatabaseConnectionException e) {
            throw new DatabaseServiceException("An error occurred while connecting to the database", e.getCause());
        } catch (SQLException e) {
            throw new DatabaseServiceException("An error occurred related to the SQL", e.getCause());
        }

        return resultArray;
    }

    /**
     * A utility method to run scripts for DB interactions
     *
     * @param script full path to the script to run to create the schema
     * @throws DatabaseInitializationException Occurs when there is an issue initializing the database.
     * @throws DatabaseConfigurationException  Thrown when the hibernate configuration file cannot be loaded.
     */
    public static void runScript(String script) throws DatabaseInitializationException, DatabaseConfigurationException {

        Connection connection = null;
        try {
            connection = getConnection();
        } catch (DatabaseConnectionException e) {
            e.printStackTrace();
        }

        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            ScriptRunner runner = new ScriptRunner(connection, false, false);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(script));

            runner.runScript(reader);
            reader.close();
            connection.commit();
            connection.close();

        } catch (DatabaseConnectionException | SQLException | IOException e) {
            throw new DatabaseInitializationException("Could not initialize db because of: "
                    + e.getMessage(), e);
        } catch (Throwable e) {
            throw new DatabaseConfigurationException("Could not load hibernate configuration file because of: "
                    + e.getMessage(), e);
        }

    }
}

