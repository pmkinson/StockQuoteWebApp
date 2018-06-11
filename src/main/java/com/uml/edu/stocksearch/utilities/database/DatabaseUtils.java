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
    private static final String HIBERNATE_PATH = "./src/main/resources/hibernate.cfg.xml";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String HIBERNATE = "hibernate.cfg.xml";

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
                    updateHibernateConfig();
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
     *
     * @throws DatabaseConfigurationException  Thrown when the URI cannot be parsed or
     *                                         if the environmental variable is null.
     */

    public static void updateHibernateConfig() throws DatabaseConfigurationException {

        try {
            File inputFile = new File(HIBERNATE_PATH);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputFile);

            Node sessionFactoryTag = doc.getElementsByTagName("session-factory").item(0);
            NodeList list = sessionFactoryTag.getChildNodes();

            //Get Heroku credentials
            ArrayList<String> credentials = getHerokuCredentials();

            //Update config file in memory
            list.item(5).setTextContent(credentials.get(0));
            list.item(7).setTextContent(credentials.get(1));
            list.item(9).setTextContent(credentials.get(2));

            //Save updated config file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(inputFile);
            DOMImplementation domImpl = doc.getImplementation();

            DocumentType doctype = domImpl.createDocumentType("doctype",
                    "\n-//Hibernate/Hibernate Configuration DTD//EN",
                    "\nhttp://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd");

            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

            transformer.transform(source, result);

        } catch (URISyntaxException | NullPointerException | IOException |
                ParserConfigurationException | SAXException | TransformerException e) {
            throw new DatabaseConfigurationException("Failed to update 'hibernate.cfg.xml' database credentials before opening a connection.", e.getCause());
        }
    }

    /**
     * Retrieve updated credentials from Heroku and parse them into a ArrayList of strings.
     *
     * @return ArrayList of updated credentials
     * @throws URISyntaxException Thrown if DATABASE_URL does not exist. This is Heroku hosting specific environmental variable.
     *                            This will be thrown if webapp is hosted by a different server.
     */
    private static ArrayList<String> getHerokuCredentials() throws URISyntaxException {
        ArrayList<String> credentials = new ArrayList<>();

        //Get Heroku credentials from local environmental variable
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        //Parse local Environmental Variable from Heroku server for current username/password and database URL.
        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String database = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        credentials.add(database);
        credentials.add(username);
        credentials.add(password);

        return credentials;
    }


}

