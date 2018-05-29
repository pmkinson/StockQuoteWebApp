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
 * package com.uml.edu.stocksearch.model;
 * <p>
 * /**
 * Empty wrapper for all DAOObject objects.
 */

package com.uml.edu.stocksearch.utilities.database;

import com.uml.edu.stocksearch.model.DAOObject;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseConnectionException;
import com.uml.edu.stocksearch.utilities.database.exceptions.DatabaseInitializationException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.io.File;

/**
 * A class that contains database related utility methods.
 */

public class DatabaseUtils {

    private static Configuration configuration;
    private static SessionFactory sessionFactory;
    private static final String HIBERNATE_PATH = "./src/main/resources/hibernate.cfg.xml";
    private static final String DRIVER = "org.postgresql.Driver";

    /**
     * Utility method to return a connection to the database
     *
     * @return <CODE>Connection</CODE> connection
     * @throws DatabaseConnectionException    Thrown when a connection cannot be established to DB
     * @throws DatabaseConfigurationException Thrown when the hibernate configuration file cannot be loaded.
     */
    public static Connection getConnection() throws DatabaseConnectionException, DatabaseConfigurationException {

        Connection connection = null;
        Configuration configuration = getConfiguration(HIBERNATE_PATH);
        try {

            Class.forName(DRIVER);

            //Make sure config file has latest credentials as well as url.
            configuration = getDBCredentials(configuration);

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
    public static SessionFactory getSessionFactory() throws DatabaseInitializationException {

        // singleton pattern
        synchronized (DatabaseUtils.class) {
            try {
                if (sessionFactory == null) {
                    sessionFactory = buildSessionFactory();
                } else {
                    return sessionFactory;
                }
            } catch (Throwable e) {
                throw new DatabaseInitializationException("Could not return a concrete session factory because of: "
                        + e.getMessage(), e);
            }
        }
        return sessionFactory;
    }

    /**
     * Method commits a DAOObject object to the database or updates
     * an entry already stored in the database.
     *
     * @param DAOObject DAOObject object to commit
     *
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
                    .configure("hibernate.cfg.xml").build();

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
     * @return A hibernate configuration file
     * @throws DatabaseConfigurationException
     */
    private static Configuration getConfiguration(String filePath) throws DatabaseConfigurationException {

        File file = new File(filePath);
        try {
            if (configuration == null) {
                configuration = new Configuration();
                configuration.configure(file);
                configuration.getProperties();
            }

        } catch (Throwable e) {
            throw new DatabaseConfigurationException("Couldn't load hibernate.cfg.cml configuration file because of: "
                    + e.getMessage(), e);
        }

        return configuration;
    }

    /**
     * This method gathers the updated database url as well as the default login / password from Heroku.
     * Each time a dyno is started for a web app instance at Heroku, an environment variable is created
     * called DATABASE_URL.  The database url, as well as the login / password are periodically rotated
     * by Heroku.  This method ensures the webapp will always have correct configuration variables.
     *
     * @param hibernateConfig The Hibernate Configuration object to update credentials
     *
     * @throws DatabaseConfigurationException  Thrown when the URI cannot be parsed or
     *                                         if the environmental variable is null.
     */

    public static Configuration getDBCredentials(Configuration hibernateConfig) throws DatabaseConfigurationException {

        try {
            //Get Heroku credentials from local environmental variable
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            //Parse local Environmental Variable from Heroku server for current username/password and database URL.
            final String username = dbUri.getUserInfo().split(":")[0];
            final String password = dbUri.getUserInfo().split(":")[1];
            final String database = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

            //Update configuration loaded into memory
            hibernateConfig.setProperty("hibernate.connection.username", username);
            hibernateConfig.setProperty("hibernate.connection.password", password);
            hibernateConfig.setProperty("hibernate.connection.url", database);

        } catch (URISyntaxException | NullPointerException e) {
            throw new DatabaseConfigurationException("Failed to update database credentials before opening a connection.", e.getCause());
        }
        return hibernateConfig;
    }


}

