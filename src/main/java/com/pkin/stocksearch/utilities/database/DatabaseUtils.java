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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

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
     * @param hibernateConfigFile The name of the hibernate config file to load for the connection.
     * @return <CODE>Connection</CODE> connection
     * @throws DatabaseConnectionException    Thrown when a connection cannot be established to DB
     * @throws DatabaseConfigurationException Thrown when the hibernate configuration file cannot be loaded.
     */
    public static Connection getConnection(String hibernateConfigFile) throws DatabaseConnectionException, DatabaseConfigurationException {

        HibernateUtils.verifyHibernateConfig(hibernateConfigFile);
        Connection connection;
        Configuration configuration = getConfiguration(hibernateConfigFile);
        try {

            Class.forName(HibernateUtils.getDriver(hibernateConfigFile));

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
    synchronized public static SessionFactory getSessionFactory(String hibernateConfigFile) throws DatabaseInitializationException {

        // singleton pattern
        try {
            if (sessionFactory == null) {
                HibernateUtils.verifyHibernateConfig(hibernateConfigFile);
                sessionFactory = buildSessionFactory(hibernateConfigFile);
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

    private static SessionFactory buildSessionFactory(String hibernateConfigFile) {

        try {

            // Create the ServiceRegistry from hibernate config file
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure(hibernateConfigFile).build();

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
     * @param hibernateConfig Hibernate config file to load
     * @param maxResults      Maximum size list of results to return
     * @param queryPopulation Number of results to return from database for frequency sorting.
     * @return String with top 5 searches formatted in HTML
     * @throws DatabaseServiceException
     */
    public static ArrayList<String> queryDBForTopSearches(String hibernateConfig, int maxResults, int queryPopulation) throws DatabaseServiceException {
        ArrayList<String> sortedResults;

        Query query;
        Session session;

        try {
            //Get a session and begin a transaction
            session = getSessionFactory(hibernateConfig).openSession();

            //Retrieve 100 recent queries
            query = session.createQuery("select stock.stockSymbol from SearchDAO stock");
            query.setMaxResults(queryPopulation);

            List list = query.list();

            HashMap<String, Integer> map = new HashMap<>();

            //Loop through the queried results
            for (int i = 0; i < list.size(); i++) {
                String stock = (String) list.get(i);

                //Loop over the k,v pairs building a frequency for stock symbols from the queried results.
                for (int b = 0; b < list.size(); b++) {
                    if (list.get(b) == stock) {
                        //See if map contains the stock.  If is doesn't add it with a value of 1.
                        if (!map.containsKey(stock)) {
                            map.put(stock, 1);
                        } else {
                            //Increment the value, aka the rolling count for the key.
                            int value = map.get(stock);
                            map.put(stock, ++value);
                        }
                    }
                }
            }
            //Sort list based on frequency.
            sortedResults = sortByKey(map, maxResults);

        } catch (DatabaseInitializationException e) {
            throw new DatabaseServiceException("An error occurred while connecting to the database", e.getCause());
        }

        return sortedResults;
    }

    /**
     * Method to sort a Map by the key
     *
     * @param map Map to sort
     * @return TreeMap sorted by value
     */
    private static ArrayList<String> sortByKey(Map map, int maxResults) {

        ArrayList<String> descList = new ArrayList<>();

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        int listCount = 0;
        int listSize = 0;

        //See if list of results is smaller than the maxResults.
        if (list.size() > maxResults) {
            listCount = list.size() - (maxResults + 1);
            listSize = list.size() - 1;
        } else {
            listCount = -1;
            listSize = list.size() - 1;
        }

        for (int i = listSize; i > listCount; i--) {
            descList.add(list.get(i).getKey());
        }


        return descList;
    }

}

