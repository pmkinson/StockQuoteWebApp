package com.uml.edu.stocksearch.utilities.database;

import com.uml.edu.stocksearch.model.DAO;
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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;


import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A class that contains database related utility methods.
 */

public class DatabaseUtils {

    private static Configuration configuration;
    private static SessionFactory sessionFactory;
    private static final String HIBERNATE_PATH = "./src/main/resources/hibernate.cfg.xml";

    /**
     * Utility method to return a connection to the database
     *
     * @return <CODE>Connection</CODE> connection
     * @throws DatabaseConnectionException    Thrown when a connection cannot be established to DB
     * @throws DatabaseConfigurationException Thrown when the hibernate configuration file cannot be loaded.
     */
    public static Connection getConnection() throws DatabaseConnectionException, DatabaseConfigurationException {

        Connection connection = null;
        Configuration configuration = getConfiguration();
        try {

            Class.forName("com.mysql.jdbc.Driver");

            updateHibernateConfig(); //Make sure config file has latest credentials as well as url.

            String databaseUrl = configuration.getProperty("connection.url");
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
     * Method commits a DAO object to the database or updates
     * an entry already stored in the database.
     *
     * @param DAOObject DAO object to commit
     * @throws DatabaseInitializationException Thrown when there is an issue
     *                                         communicating with database.
     * @throws DatabaseConnectionException     Error will be thrown when the
     *                                         the object in the database cannot
     *                                         be updated or a new object cannot
     *                                         be added to the database.
     */

    synchronized public static void commitDAOObject(DAO DAOObject) throws DatabaseConnectionException, DatabaseInitializationException {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //Commit DAO object to DB.
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
    private static Configuration getConfiguration() throws DatabaseConfigurationException {

        File file = new File(path);
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
     * @throws URISyntaxException
     */
    public static void updateHibernateConfig() throws DatabaseConfigurationException {

        try {
            configuration = getConfiguration();
            URI dbUri = new URI(System.getenv("DATABASE_URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);
            configuration.setProperty("connection.url", dbUrl);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(HIBERNATE_PATH);

        } catch (URISyntaxException e) {
            throw new DatabaseConfigurationException("Failed to read DATABASE_URL into a new URI", e.getCause());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new DatabaseConfigurationException("Failedto update the hibernate config file", e.getCause());
        }

    }


}

