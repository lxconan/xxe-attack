package org.xxeAttack;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.xxeAttack.XmlUtil.*;

class XxeAttackTest {
    @Test
    void should_parse_xml_document() throws Exception {
        final String xml =
            "<?xml version=\"1.0\"?>\n" +
            "<note>\n" +
            "  <to>Tove</to>\n" +
            "  <from>Jani</from>\n" +
            "</note>";

        final Document document = parseDocument(xml);
        final String from = getElementContent(document, "from");

        assertEquals("Jani", from);
    }

    @Test
    void should_success_if_xml_document_is_valid() throws Exception {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!DOCTYPE blog [ \n" +
            "    <!ELEMENT blog ANY >\n" +
            "    <!ELEMENT author (#PCDATA) >\n" +
            "]> \n" +
            "<blog>\n" +
            "    <author>Super Cool Name</author> \n" +
            "</blog>\n";

        assertDoesNotThrow(() -> parseDocument(xml, true));
    }

    @Test
    void should_throw_if_xml_document_is_invalid() throws Exception {
        final String invalidXml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!DOCTYPE blog [ \n" +
            "    <!ELEMENT blog ANY >\n" +
            "    <!ELEMENT author (#PCDATA) >\n" +
            "]> \n" +
            "<invalidRoot>\n" +
            "    <author>Super Cool Name</author> \n" +
            "</invalidRoot>\n";

        assertThrows(SAXParseException.class, () -> parseDocument(invalidXml, true));
    }

    @Test
    void should_replace_internal_entity_value() throws Exception {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!DOCTYPE blog [ \n" +
            "    <!ELEMENT blog ANY >\n" +
            "    <!ELEMENT author (#PCDATA) >\n" +
            "    <!ENTITY authorName \"Bob\" >\n" +
            "]> \n" +
            "<blog>\n" +
            "    <author>&authorName;</author> \n" +
            "</blog>\n";

        final Document document = parseDocument(xml, true);
        final String authorName = getElementContent(document, "author");

        assertEquals("Bob", authorName);
    }

    @Test
    void should_replace_external_entity() throws Exception {
        // TODO: You may want to change this.
        final String externalUri = "file:///home/liuxia/MyProject/xxe-attack/password.txt";

        final String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!DOCTYPE blog [ \n" +
            "    <!ELEMENT blog ANY >\n" +
            "    <!ELEMENT password (#PCDATA) >\n" +
            "    <!ENTITY passwordContent SYSTEM \"" + externalUri + "\" >\n" +
            "]> \n" +
            "<blog>\n" +
            "    <password>&passwordContent;</password> \n" +
            "</blog>\n";

        final Document document = parseDocument(xml, true);
        final String password = getElementContent(document, "password");

        assertEquals("This is my password: O_o", password);
    }

    @Test
    void should_prevent_external_entity() throws Exception {
        // TODO: You may want to change this.
        final String externalUri = "file:///home/liuxia/MyProject/xxe-attack/password.txt";

        final String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!DOCTYPE blog [ \n" +
                "    <!ELEMENT blog ANY >\n" +
                "    <!ELEMENT password (#PCDATA) >\n" +
                "    <!ENTITY passwordContent SYSTEM \"" + externalUri + "\" >\n" +
                "]> \n" +
                "<blog>\n" +
                "    <password>&passwordContent;</password> \n" +
                "</blog>\n";

        assertThrows(SAXParseException.class, () -> parseDocumentSafely(xml));
    }
}