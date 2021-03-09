package org.xxeAttack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class XmlUtil {
    public static String getElementContent(Document docuemnt, String tagName) {
        final StringBuilder stringBuilder = new StringBuilder();
        final NodeList nodeList = docuemnt.getElementsByTagName(tagName);
        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); ++nodeIndex) {
            final Node item = nodeList.item(nodeIndex);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                final Element element = (Element) item;
                stringBuilder.append(element.getTextContent());
            }
        }

        return stringBuilder.toString();
    }

    public static Document parseDocument(
        String xml,
        boolean shouldValidateDtd) throws Exception {
        try (
            final InputStream stream = new ByteArrayInputStream(xml.getBytes())
        ) {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(shouldValidateDtd);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if (shouldValidateDtd) {
                documentBuilder.setErrorHandler(createErrorHandler());
            }
            return documentBuilder.parse(stream);
        }
    }

    private static ErrorHandler createErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void warning(SAXParseException e) throws SAXException {
                throw e;
            }

            @Override
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                throw e;
            }
        };
    }

    public static Document parseDocument(String xml) throws Exception {
        return parseDocument(xml, false);
    }
}
