package com.progressoft.induction.tp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;


public class XMLProcessor extends TransactionProcessorImpl {


    @Override
    public void importTransactions(InputStream is) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            int i = 0;
            try {
                while (doc.getStrictErrorChecking()) {
                    Element transaction = (Element) doc.getDocumentElement().getElementsByTagName("Transaction").item(i);
                    String amount = transaction.getAttributes().getNamedItem("amount").getNodeValue();
                    Transaction newTransaction = new Transaction(transaction.getAttributes().getNamedItem("type").getNodeValue(),
                            tryReadAmount(amount), transaction.getAttributes().getNamedItem("narration").getNodeValue());
                    transactionList.add(newTransaction);
                    i++;
                }
            } catch (NullPointerException e) {
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    @Override
//    public void importTransactions(InputStream is) {
//
//        try {
//
//            XMLInputFactory factory = XMLInputFactory.newInstance();
//            XMLEventReader eventReader = factory.createXMLEventReader(is);
//
//            while (eventReader.hasNext()) {
//
//                XMLEvent event = eventReader.nextEvent();
//
//                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
//
//                    StartElement startElement = event.asStartElement();
//                    String qName = startElement.getName().getLocalPart();
//
//                    if (qName.equalsIgnoreCase("transaction")) {
//
//                        Iterator<Attribute> attributes = startElement.getAttributes();
//
//                        String type = "";
//                        String amount = "";
//                        String narration = "";
//
//                        for (int i = 0; i < 3; i++) {
//                            Attribute myAttribute = attributes.next();
//                            String attName = myAttribute.getName().toString();
//                            String attValue = myAttribute.getValue();
//
//                            if (attName == "type") {
//                                type = attValue;
//                            } else if (attName == "amount") {
//                                amount = attValue;
//                            } else if (attName == "narration") {
//                                narration = attValue;
//                            }
//                        }
//
//                        BigDecimal bdAmount = tryReadAmount(amount);
//
//                        Transaction newTransaction = new Transaction(type, bdAmount, narration);
//                        transactionList.add(newTransaction);
//                    }
//
//                }
//            }
//
//
//        } catch (Exception e) {
//        }
//    }
}




