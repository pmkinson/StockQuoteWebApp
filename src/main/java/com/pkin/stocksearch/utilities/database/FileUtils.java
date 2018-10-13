package com.pkin.stocksearch.utilities.database;

import com.opencsv.CSVReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FileUtils {


    /**
     * Method to read the context of an attribute from a NodeList.
     * Will return null if not match is found.
     *
     * @param nodeList       NodeList to read from
     * @param attribute      The node-element attribute to search for. Example attribute is "name".
     * @param attributeMatch The value of the node-element attribute to match. Example "name='John' "
     * @return String representation of the node-element context.
     */
    static String readXmlContext(NodeList nodeList, String attribute, String attributeMatch) {

        String backEndVariable = "";
        for (int element = 0; element < nodeList.getLength(); element++) {
            Node node = nodeList.item(element);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;
                //Loop through nodes looking for a match to the method signature argument
                if (nodeElement.getAttribute(attribute).equals(attributeMatch)) {
                    //Retrieve variable from xml
                    backEndVariable = nodeList.item(element).getTextContent();
                    return backEndVariable;
                }
            }
        }

        return backEndVariable;
    }

    /**
     * Build a NodeList from an XML document
     *
     * @param document The document to read.
     * @param element  The parent element to build a nodelist from.
     * @return A NodeList
     */
    static NodeList getNodeList(Document document, String element) {

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
     * Method to read a CSV formatted file into an array.
     *
     * @param fileName The CSV File to read in.
     * @return An ArrayList<String> with the contents of the CSV File.
     * @throws FileNotFoundException
     */

    public static String getCSVFile(String fileName) throws IOException, URISyntaxException {

        File file = getFile(fileName);

        CSVReader csvReader = new CSVReader(new FileReader(file));
        StringBuilder cvsString = new StringBuilder();

        int counter = 0;
        while (csvReader.readNext() != null) {

            String[] field = csvReader.readNext();
            if (counter == 0) {
                cvsString.append("\"" + field[0] + "     ( " + field[1] + " )\"");
            } else {
                cvsString.append(", \"" + field[0] + "     ( " + field[1] + " )\"");
            }
            counter++;
        }

        return cvsString.toString();
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
    static Document xmlDocument(String filePath) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {

        File inputFile = getFile(filePath);
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        //Disable downloading external dtd
        documentFactory.setValidating(false);
        documentFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder docBuilder = documentFactory.newDocumentBuilder();
        Document document = docBuilder.parse(inputFile);

        return document;
    }
}
