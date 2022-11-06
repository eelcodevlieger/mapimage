package com.knocksfornometer.mapimage.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlUtils {

	public static Document loadAsXmlDocument(File xmlFile) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		return domFactory.newDocumentBuilder().parse(xmlFile.getAbsolutePath());
	}

	public static void writeXmlDocumentToFile(Document doc, String fileName) throws TransformerFactoryConfigurationError, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult( new File(fileName) ));
	}
}