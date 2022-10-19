package edu.java.spring.utils;

import edu.java.spring.model.JavaClazz;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class XSLTUtils {

    public static DOMSource clazzToDomSource(JavaClazz clazz) throws JAXBException, ParserConfigurationException, IOException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(JavaClazz.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        jaxbMarshaller.marshal(clazz, outputStream);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(outputStream.toByteArray()));

        return new DOMSource(document);
    }
}
