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
import com.pkin.stocksearch.model.DAOObject;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;

/**
 * A class that contains database related utility methods.
 */

public class DatabaseUtils {

    private static Configuration configuration;
    private static SessionFactory sessionFactory;

    //All these constants are related to the hibernate config file
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String HIBERNATE = "hibernate.cfg.xml";
    private static final String PARENT_NODE = "session-factory";
    private static final String PUBLIC_ID = "\n-//Hibernate/Hibernate Configuration DTD//EN";
    private static final String SYSTEM_ID = "\nhttp://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd";

    /**
     * Utility method to return a connection to the database
     *
     * @return <CODE>Connection</CODE> connection
     * @throws DatabaseConnectionException    Thrown when a connection cannot be established to DB
     * @throws DatabaseConfigurationException Thrown when the hibernate configuration file cannot be loaded.
     */
    public static Connection getConnection() throws DatabaseConnectionException, DatabaseConfigurationException {

        HibernateUtils.verifyHibernateConfig();
        Connection connection = null;
        Configuration configuration = getConfiguration(HIBERNATE);
        try {

            Class.forName(DRIVER);

            String databaseUrl = configuration.getProperty("hibernate.connection.url");
            String username = configuration.getProperty("hibernate.connection.username");
            String password = configuration.getProperty("hibernate.connection.password");

            connection = DriverManager.getConnection(databaseUrl, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseConnectionException("Could not open a connection to database." + e.getMessage(), e);
        }
        return connection;
    }

    /**
     * Method to shutdown the active session factory.
     *
     * @throws DatabaseConnectionException Occurs when the connection to
     *                                     the database cannot be terminated.
     */
    public static void shutdown() throws DatabaseConnectionException {
        // Close caches and connection pools
        try {
            getSessionFactory().close();
        } catch (Throwable e) {
            throw new DatabaseConnectionException("An error occured while closing the active DB session" +
                    " because of: " + e.getMessage(), e);
        }
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
                HibernateUtils.verifyHibernateConfig();
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
     * Method commits a DAOObject object to the database or updates
     * an entry already stored in the database.
     *
     * @param DAOObject DAOObject object to commit
     * @throws DatabaseInitializationException Thrown when there is an issue
     *                                         communicating with database.
     * @throws DatabaseConnectionException     Error will be thrown when the
     *                                         the object in the database cannot
     *                                         be updated or a new object cannot
     *                                         be added to the database.
     */

    synchronized public static void commitDAOObject(DAOObject DAOObject) throws DatabaseConnectionException, DatabaseInitializationException {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //Commit DAOObject object to DB.
            session.saveOrUpdate(DAOObject);
            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();  // close transaction
            }
            throw new DatabaseConnectionException("Could not add or update " + DAOObject.toString()
                    + " data. " + e.getMessage(), e);
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.commit();
            }
        }
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


}

