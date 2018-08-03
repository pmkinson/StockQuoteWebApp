package com.pkin.stocksearch.utilities.database;

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

public class SupportMethods {

    private final String PUBLIC_ID = "\n-//Hibernate/Hibernate Configuration DTD//EN";
    private final String SYSTEM_ID = "\nhttp://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd";

    public SupportMethods() {
    }

    public void changeConfigFile(int index, String var) {

        try {
            //Load File
            URI uri = HibernateUtilsTest.class.getClassLoader().getResource("hibernate.cfg.xml").toURI();
            File inputFile = new File(uri);

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = documentFactory.newDocumentBuilder();
            Document document = docBuilder.parse(inputFile);

            Node sessionFactoryTag = document.getElementsByTagName("session-factory").item(0);
            NodeList list = sessionFactoryTag.getChildNodes();

            //Update File
            list.item(index).setTextContent(var);

            //Save File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            File file = inputFile;
            StreamResult result = new StreamResult(file);
            DOMImplementation domImplementation = document.getImplementation();

            DocumentType documentType = domImplementation.createDocumentType("doctype", PUBLIC_ID, SYSTEM_ID);

            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());

            //Save
            transformer.transform(source, result);

        } catch (URISyntaxException | IOException | SAXException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

    }
}
