package com.pkin.stocksearch.utilities.database;

import com.pkin.stocksearch.utilities.database.exceptions.DatabaseConfigurationException;
import com.pkin.stocksearch.utilities.database.exceptions.DatabaseInitializationException;
import org.hibernate.cfg.Configuration;


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
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SupportMethods {

    private final String PUBLIC_ID = "\n-//Hibernate/Hibernate Configuration DTD//EN";
    private final String SYSTEM_ID = "\nhttp://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd";

    private Configuration configuration;

    public SupportMethods() {
    }

    public void resetMainHibernateFile() {

        ClassLoader classLoader = getClass().getClassLoader();
        File mainConfigFile = new File(classLoader.getResource("hibernate-backup.cfg.xml").getFile());
        File testConfigFile = new File(classLoader.getResource("hibernate.cfg.xml").getFile());

        //Delete test config file.
        testConfigFile.delete();

        //Rename main config file back to hibernate.cfg.xml
        File renameMainConfig = new File(mainConfigFile.getParent().concat("\\hibernate.cfg.xml"));
        mainConfigFile.renameTo(renameMainConfig);
    }

    public void copyMainFile() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File mainConfig = new File(classLoader.getResource("hibernate.cfg.xml").getFile());
            File testConfig = new File(classLoader.getResource("hibernate-test.cfg.xml").getFile());

            //Rename main hibernate.cfg.xml file
            File renameMainConfig = new File(mainConfig.getParent().concat("\\hibernate-backup.cfg.xml"));
            mainConfig.renameTo(renameMainConfig);

            Files.copy(testConfig.toPath(), mainConfig.toPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeConfigFile(String nodeAttribute, String nodeContext, String fileName,
                                 String mainFileName, boolean modifyMainFile) {

        try {

            //Load File
            ClassLoader classLoader = getClass().getClassLoader();

            File inputFile = new File(classLoader.getResource(fileName).getFile());

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = documentFactory.newDocumentBuilder();
            Document document = docBuilder.parse(inputFile);

            Node sessionFactoryTag = document.getElementsByTagName("session-factory").item(0);
            NodeList list = sessionFactoryTag.getChildNodes();

            for (int element = 0; element < list.getLength(); element++) {
                Node node = list.item(element);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) node;
                    //Loop through nodes looking for a match to the method signature argument
                    if (nodeElement.getAttribute("name").equals(nodeAttribute)) {
                        String contents = nodeElement.getTextContent();
                        String elementName = nodeElement.getAttribute("name");
                        //Update Element
                        nodeElement.setTextContent(nodeContext);
                    }
                }
            }

            //Save File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(inputFile);
            DOMImplementation domImplementation = document.getImplementation();

            DocumentType documentType = domImplementation.createDocumentType("doctype", PUBLIC_ID, SYSTEM_ID);

            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());

            transformer.transform(source, result);

        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }

    /**
     * A utility method to run scripts for DB interactions
     *
     * @param script full path to the script to run to create the schema
     * @throws DatabaseInitializationException Occurs when there is an issue initializing the database.
     * @throws DatabaseConfigurationException  Thrown when the hibernate configuration file cannot be loaded.
     */
    public void runScript(String script) throws DatabaseInitializationException, DatabaseConfigurationException {

        Connection connection = null;

        URL path = SupportMethods.class.getClassLoader().getResource(script);
        String path2 = path.toString().substring(6, path.toString().length());
        try {
            String driver = "org.apache.derby.jdbc.EmbeddedDriver";
            Class.forName(driver).newInstance();

            String protocol = "jdbc:derby:TEST_DB;create=true";

            connection = DriverManager.getConnection(protocol);
            connection.setAutoCommit(false);
            com.ibatis.common.jdbc.ScriptRunner scriptRunner;
            scriptRunner = new com.ibatis.common.jdbc.ScriptRunner(connection, false, false);
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path2));

            scriptRunner.runScript(reader);
            reader.close();
            connection.commit();
            connection.close();

        } catch (SQLException | IOException | NullPointerException e) {
            throw new DatabaseInitializationException("Could not initialize db because of: "
                    + e.getMessage(), e);
        } catch (Throwable e) {
            throw new DatabaseConfigurationException("Could not load hibernate configuration file because of: "
                    + e.getMessage(), e);
        }

    }

    /**
     * Kill the local Derby test db instance
     */

    public void shutdownTestDB(String dbName) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
