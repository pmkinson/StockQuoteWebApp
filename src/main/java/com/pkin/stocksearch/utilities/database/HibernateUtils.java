package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.HibernateUtilitiesException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class HibernateUtils {

    private static final String HIBERNATE = "hibernate.cfg.xml";
    private static final String PARENT_NODE = "session-factory";
    private static final String PUBLIC_ID = "\n-//Hibernate/Hibernate Configuration DTD//EN";
    private static final String SYSTEM_ID = "\nhttp://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd";

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
     * @param configPath Path to the hibernate.cfg.xml file
     * @throws DatabaseConfigurationException Wrapper exception for any error that occurs
     *                                        while updating the hibernate.cfg.xml file
     */

    public static void verifyHibernateConfig(String configPath) throws DatabaseConfigurationException {

        try {
            Document document = xmlDocument(configPath);
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
    public static File getFile(String file) throws URISyntaxException {
        URI uri = getResourceUri(file);

        File returnFile = new File(uri);
        return returnFile;
    }

    /**
     * Method to return the URI of a file
     *
     * @param file Path to file
     * @return URI
     * @throws URISyntaxException
     */
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

        //Disable downloading external dtd
        documentFactory.setValidating(false);
        documentFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

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
     * Method to retrieve the hibernate.driver value from the
     * hibernate.cfg.xml config file.
     *
     * @return String representation of the driver value
     * @throws HibernateUtilitiesException Thrown for any underlying issue with retrieving the driver value.
     */
    public static String getDriver() throws HibernateUtilitiesException {
        String driver;

        try {
            Document document = xmlDocument(HIBERNATE);
            NodeList nodeList = getHibernateNodeList(document, PARENT_NODE);

            driver = nodeList.item(3).getTextContent();

        } catch (URISyntaxException | ParserConfigurationException | SAXException | IOException e) {
            throw new HibernateUtilitiesException("An error occurred while retrieving the " +
                    "driver value from the hibernate.cfg.xml file", e);
        }

        return driver;
    }

}
