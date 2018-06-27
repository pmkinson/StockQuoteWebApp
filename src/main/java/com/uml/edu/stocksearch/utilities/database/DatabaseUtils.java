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

package com.uml.edu.stocksearch.utilities.database;

import com.uml.edu.stocksearch.model.DAOObject;
import com.uml.edu.stocksearch.service.exceptions.DatabaseServiceException;
import com.uml.edu.stocksearch.utilities.WebUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

        verifyHibernateConfig();
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
                verifyHibernateConfig();
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
            File file = getFile(filePath);

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
     * This method will read in the Hibernate config file located in the resources folder.
     * The purpose of this method is act as a controller for automatically updating the
     * config file based on the needs of wherever the app is being hosted.
     * <p>
     * To configure what case is run, open the hibernate.cfg.xml file and change the content for;
     * < property name="backend">##</property>
     * <p>
     * Current Assignments:
     * 0 - Does nothing.  Will not update the config file.
     * 1 - Update config for Heroku hosting.
     * 2 - Ready for development.
     * <p>
     * Heroku app hosting services have rotating authentication credentials for their
     * free database. This ensures that the most recent credentials are retrieved at app
     * instantiation.
     *
     * @throws DatabaseConfigurationException Wrapper exception for any error that occurs
     *                                        while updating the hibernate.cfg.xml file
     */

    public static void verifyHibernateConfig() throws DatabaseConfigurationException {

        try {
            Document document = xmlDocument(HIBERNATE);
            NodeList nodeList = getHibernateNodeList(document, PARENT_NODE);

            String backEndVariable = nodeList.item(11).getTextContent();

            switch (backEndVariable) {
                case "0": {
                    //Do not update the config file.
                    break;
                }
                case "1": {
                    updateHerokuCredentials(nodeList);
                    //Save updated config file
                    saveHibernateConfig(document);
                    break;
                }
                case "2": {
                    //Add next scenario with which to change hibernate config file
                    break;
                }
                default: {
                    //Do nothing.
                    break;
                }
            }

        } catch (URISyntaxException | NullPointerException | IOException |
                ParserConfigurationException | SAXException | TransformerException e) {
            throw new DatabaseConfigurationException("Failed to update 'hibernate.cfg.xml' database credentials before opening a connection.", e.getCause());
        }
    }

    /**
     * Build a NodeList from an XML document
     *
     * @param document The document to read.
     * @param element  The parent element to build a nodelist from.
     * @return A NodeList
     */
    private static NodeList getHibernateNodeList(Document document, String element) {

        Node sessionFactoryTag = document.getElementsByTagName(element).item(0);
        NodeList list = sessionFactoryTag.getChildNodes();

        return list;
    }

    /**
     * Method to create a file object. Reducing repetative code.
     *
     * @param file String for the filepath
     * @return A File object.
     */
    private static File getFile(String file) throws URISyntaxException {
        URI uri = getResourceUri(file);

        File returnFile = new File(uri);
        return returnFile;
    }

    private static URI getResourceUri(String file) throws URISyntaxException {
        URI uri = DatabaseUtils.class.getClassLoader().getResource(file).toURI();
        return uri;
    }

    /**
     * Method to retrieve and parse the Hibernate Config file into XML format for editing.
     *
     * @return A Document object
     * @throws ParserConfigurationException Thrown when file contains invalid XML and cannot be parsed
     * @throws IOException                  Thrown when there's an error locating hibernate.cfg.xml
     * @throws SAXException                 Thrown for general exception for any SAX errors that may occur.
     */
    private static Document xmlDocument(String filePath) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {

        File inputFile = getFile(HIBERNATE);
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = documentFactory.newDocumentBuilder();
        Document document = docBuilder.parse(inputFile);

        return document;
    }

    /**
     * Retrieve updated credentials from Heroku and parse them into a ArrayList of strings.
     *
     * @return ArrayList of updated credentials
     * @throws URISyntaxException Thrown if DATABASE_URL does not exist. This is Heroku hosting specific environmental variable.
     *                            This will be thrown if webapp is hosted by a different server.
     */
    private static ArrayList<String> updateHerokuCredentials(NodeList nodeList) throws URISyntaxException {
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

        //Update config file in memory
        nodeList.item(5).setTextContent(credentials.get(0));
        nodeList.item(7).setTextContent(credentials.get(1));
        nodeList.item(9).setTextContent(credentials.get(2));

        return credentials;
    }

    /**
     * Save changes to the hibernate config file.
     *
     * @param document Hiberrnate config file
     * @throws TransformerException Generalized exception thrown if there's an error saving the file.
     */
    private static void saveHibernateConfig(Document document) throws TransformerException, URISyntaxException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        File file = getFile(HIBERNATE);
        StreamResult result = new StreamResult(file);
        DOMImplementation domImplementation = document.getImplementation();

        DocumentType documentType = domImplementation.createDocumentType("doctype", PUBLIC_ID, SYSTEM_ID);

        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());

        transformer.transform(source, result);

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
            //Returns top 5 searches
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

