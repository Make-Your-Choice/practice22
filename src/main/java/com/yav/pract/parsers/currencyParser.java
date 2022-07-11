package com.yav.pract.parsers;

import com.yav.pract.dao.currencyDAO;
import com.yav.pract.models.currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class currencyParser {
    @Autowired
    public currencyParser() {
    }
    public void parseByDayOrPeriod (currencyDAO dao, Date date1, Date date2, String cbId, List<currency> currList, int type) throws IOException, SAXException, ParserConfigurationException, ParseException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        SimpleDateFormat frmt = new SimpleDateFormat("dd.MM.yyyy");
        String dateFormat1 = frmt.format(date1);
        String url = "";
        if(date2 == null) {
            url = "https://cbr.ru/scripts/XML_daily.asp?date_req=" + dateFormat1;
        }
        else {
            String dateFormat2 = frmt.format(date2);
            url = "https://cbr.ru/scripts/XML_dynamic.asp?date_req1=" + dateFormat1 + "&date_req2=" + dateFormat2 + "&VAL_NM_RQ=" + cbId;
        }
        Document document = documentBuilder.parse(url);
        Node root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        List<currency> listCurrency = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            NamedNodeMap attr = node.getAttributes();
            if (node.getNodeType() != Node.TEXT_NODE) {
                NodeList currProps = node.getChildNodes();
                currency newCurrency = new currency();
                if(date2 == null) {
                    newCurrency.setCbId(attr.getNamedItem("ID").getNodeValue());
                    newCurrency.setDateRec(date1);
                }
                else {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                    String dateString = attr.getNamedItem("Date").getNodeValue();
                    newCurrency.setDateRec(formatter.parse(dateString));
                    newCurrency.setCbId(attr.getNamedItem("Id").getNodeValue());
                }
                for(int j = 0; j < currProps.getLength(); j++) {
                    Node currProp = currProps.item(j);
                    if (currProp.getNodeType() != Node.TEXT_NODE) {
                        switch (currProp.getNodeName()) {
                            case "Nominal": {
                                newCurrency.setNominal(Integer.parseInt(currProp.getChildNodes().item(0).getNodeValue()));
                            } break;
                            case "Value": {
                                newCurrency.setValue(Double.parseDouble(currProp.getChildNodes()
                                        .item(0).getNodeValue().replace(",", ".")));
                            } break;
                        }
                    }
                }
                listCurrency.add(newCurrency);
                boolean flag = false;
                if(date2 != null) {
                    if(currList != null) {
                        for(int k = 0; k < currList.size(); k++) {

                            if(currList.get(k).getDateRec().compareTo(newCurrency.getDateRec()) == 0) {
                                flag = true;
                            }
                        }
                        if(!flag) {
                            dao.updateCurrencyPeriod(newCurrency);
                        }
                    }
                    else {
                        currency tempCurr = dao.searchByDateCbIdCurrencyPeriod(newCurrency.getDateRec(), newCurrency.getCbId());
                        if(tempCurr == null) {
                            dao.updateCurrencyPeriod(newCurrency);
                        }
                    }
                }
                else {
                    switch (type) {
                        case 1: {
                            currency tempCurr = dao.searchByDateCbIdCurrencyDay(newCurrency.getDateRec(), newCurrency.getCbId());
                            if (tempCurr == null) {
                                dao.updateCurrencyDay(newCurrency);
                            } break;
                        }
                        case 2: {
                            if(newCurrency.getCbId().compareTo(cbId) == 0) {
                                currency tempCurr = dao.searchByDateCbIdCurrencyPeriod(newCurrency.getDateRec(), newCurrency.getCbId());
                                if (tempCurr == null) {
                                    dao.updateCurrencyPeriod(newCurrency);
                                }
                            } break;
                        }
                    }
                }

                /*if (date2 == null) {
                    currency tempCurr1 = dao.searchByDateCbIdCurrencyVal(newCurrency.getDateRec(), newCurrency.getCbId());
                    if(tempCurr1 == null) {
                        dao.updateCurrencyVal(newCurrency);
                    }
                }
                else {
                    currency tempCurr2 = dao.searchByDateCbIdPCurrencyVal(newCurrency.getDateRec(), newCurrency.getCbIdP());
                    if(tempCurr2 == null) {
                        dao.updateCurrencyVal(newCurrency);
                    }
                }*/
            }
        }
    }
    public void parseArchive (currencyDAO dao, int d) throws IOException, SAXException, ParserConfigurationException, ParseException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(d);
        String par = sb.toString();
        String url = "https://cbr.ru/scripts/XML_valFull.asp?d=" + par;
        Document document = documentBuilder.parse(url);
        Node root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        List<currency> listCurrency = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            NamedNodeMap attr = node.getAttributes();
            if (node.getNodeType() != Node.TEXT_NODE) {
                NodeList currProps = node.getChildNodes();
                currency newCurrency = new currency();
                newCurrency.setCbId(attr.getNamedItem("ID").getNodeValue());
                for(int j = 0; j < currProps.getLength(); j++) {
                    Node currProp = currProps.item(j);
                    if (currProp.getNodeType() != Node.TEXT_NODE) {
                        switch (currProp.getNodeName()) {
                            case "ISO_Num_Code": {
                                if(currProp.getChildNodes().item(0) == null) { newCurrency.setNumCode(null); }
                                else { newCurrency.setNumCode(currProp.getChildNodes().item(0).getNodeValue()); }
                            }
                            break;
                            case "ISO_Char_Code": {
                                if(currProp.getChildNodes().item(0) == null) { newCurrency.setCharCode(null); }
                                else { newCurrency.setCharCode(currProp.getChildNodes().item(0).getNodeValue()); }
                            }
                            break;
                            case "ParentCode": {
                                newCurrency.setCbIdP(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "Name": {
                                newCurrency.setName(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "Nominal": {
                                newCurrency.setNominal(Integer.parseInt(currProp.getChildNodes()
                                        .item(0).getNodeValue()));
                            } break;
                        }
                    }
                }
                listCurrency.add(newCurrency);
                currency tempCurr = dao.searchByCbIdArchive(newCurrency.getCbId());
                if(tempCurr == null) {
                    dao.updateArchive(newCurrency);
                }
            }
        }
    }
    /*public void parseByDate (currencyDAO dao, Date date) throws IOException, SAXException, ParserConfigurationException, ParseException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        SimpleDateFormat frmt = new SimpleDateFormat("dd.MM.yyyy");
        String dateFormat = frmt.format(date);
        String url = "https://cbr.ru/scripts/XML_daily.asp?date_req=" + dateFormat;
        Document document = documentBuilder.parse(url);
        Node root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        List<currency> listCurrency = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            NamedNodeMap attr = node.getAttributes();
            if (node.getNodeType() != Node.TEXT_NODE) {
                NodeList currProps = node.getChildNodes();
                currency newCurrency = new currency();
                newCurrency.setCbId(attr.getNamedItem("ID").getNodeValue());
                newCurrency.setDateRec(date);
                for(int j = 0; j < currProps.getLength(); j++) {
                    Node currProp = currProps.item(j);
                    if (currProp.getNodeType() != Node.TEXT_NODE) {
                        if(currProp.getNodeName().equals("Value")) {
                            newCurrency.setValue(Double.parseDouble(currProp.getChildNodes()
                                    .item(0).getNodeValue().replace(",", ".")));
                        }
                        switch (currProp.getNodeName()) {
                            case "NumCode": {
                                newCurrency.setNumCode(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "CharCode": {
                                newCurrency.setCharCode(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "Name": {
                                newCurrency.setName(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "Nominal": {
                                newCurrency.setNominal(Integer.parseInt(currProp.getChildNodes().item(0).getNodeValue()));
                            } break;
                            case "Value": {
                                newCurrency.setValue(Double.parseDouble(currProp.getChildNodes()
                                        .item(0).getNodeValue().replace(",", ".")));
                            } break;
                        }
                    }
                }
                listCurrency.add(newCurrency);
                currency tempCurr = dao.searchByDateCbIdCurrencyVal(newCurrency.getDateRec(), newCurrency.getCbId());
                if(tempCurr == null) {
                    dao.updateCurrencyVal(newCurrency);
                }
                //dao.save(newCurrency, date);
            }
        }
    }*/
    /*public static void main(String[] args) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/currDB");
        dataSource.setUsername("postgres");
        dataSource.setPassword("1234");
        currencyDAO dao = new currencyDAO(new JdbcTemplate(dataSource));
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.newDocument();
            Date date = new Date();
            SimpleDateFormat frmt1 = new SimpleDateFormat("dd.MM.yyyy");
            String dateFormat1 = frmt1.format(date);
            String url = "https://cbr.ru/scripts/XML_daily.asp?date_req=" + date;
            //date.replaceAll("/", ".");
            document = documentBuilder.parse(url);
            //Document document = documentBuilder.parse("https://cbr.ru/scripts/XML_daily.asp?date_req=02/03/2002.xml");

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            System.out.println("List of books:");
            System.out.println();
            // Просматриваем все подэлементы корневого - т.е. книги
            NodeList nodesCurr = root.getChildNodes();
            List<currency> listCurrency = new ArrayList<>();
            for (int i = 0; i < nodesCurr.getLength(); i++) {
                Node node = nodesCurr.item(i);
                NamedNodeMap attr = node.getAttributes();
                // Если нода не текст, то это книга - заходим внутрь
                if (node.getNodeType() != Node.TEXT_NODE) {
                    NodeList bookProps = node.getChildNodes();
                    currency newCurrency = new currency();
                    newCurrency.setCbId(attr.getNamedItem("ID").getNodeValue());
                    //System.out.println(newCurrency.getCbId());
                    for(int j = 0; j < bookProps.getLength(); j++) {
                        Node bookProp = bookProps.item(j);
                        // Если нода не текст, то это один из параметров книги - печатаем
                        if (bookProp.getNodeType() != Node.TEXT_NODE) {
                            newCurrency.setDateRec(date);
                            switch (bookProp.getNodeName()) {
                                case "NumCode": {
                                    newCurrency.setNumCode(bookProp.getChildNodes().item(0).getNodeValue());
                                    //System.out.println(newCurrency.getNumCode());
                                } break;
                                case "CharCode": {
                                    newCurrency.setCharCode(bookProp.getChildNodes().item(0).getNodeValue());
                                    //System.out.println(newCurrency.getCharCode());
                                } break;
                                case "Name": {
                                    newCurrency.setName(bookProp.getChildNodes().item(0).getNodeValue());
                                    //System.out.println(newCurrency.getName());
                                } break;
                                case "Value": {
                                    newCurrency.setValue(Double.parseDouble(bookProp.getChildNodes().item(0).getNodeValue().replace(",", ".")));
                                    System.out.println(newCurrency.getValue());
                                } break;
                            }
                            //System.out.println(bookProp.getChildNodes().item(0).getNodeValue());
                        }
                    }
                    listCurrency.add(newCurrency);
                    //dao.save(newCurrency);
                    System.out.println("===========>>>>");
                }
            }
            for(int i = 0; i < listCurrency.size(); i++) {
                System.out.println(listCurrency.get(i).getCbId());
                System.out.println(listCurrency.get(i).getNumCode());
                System.out.println(listCurrency.get(i).getCharCode());
                System.out.println(listCurrency.get(i).getName());
                System.out.println(listCurrency.get(i).getValue());

                System.out.println("===========>>>>");
            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }*/
    /*public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(0);
        String par = sb.toString();
        String url = "https://cbr.ru/scripts/XML_valFull.asp?d=" + par;
        Document document = documentBuilder.parse(url);
        Node root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        List<currency> listCurrency = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            NamedNodeMap attr = node.getAttributes();
            if (node.getNodeType() != Node.TEXT_NODE) {
                NodeList currProps = node.getChildNodes();
                currency newCurrency = new currency();
                newCurrency.setCbId(attr.getNamedItem("ID").getNodeValue());
                for(int j = 0; j < currProps.getLength(); j++) {
                    Node currProp = currProps.item(j);
                    if (currProp.getNodeType() != Node.TEXT_NODE) {
                        switch (currProp.getNodeName()) {
                            case "ISO_Num_Code": {
                                if(currProp.getChildNodes().item(0) == null) { newCurrency.setNumCode(null); }
                                else { newCurrency.setNumCode(currProp.getChildNodes().item(0).getNodeValue()); }
                            }
                            break;
                            case "ISO_Char_Code": {
                                if(currProp.getChildNodes().item(0) == null) { newCurrency.setCharCode(null); }
                                else { newCurrency.setCharCode(currProp.getChildNodes().item(0).getNodeValue()); }
                            }
                            break;
                            case "ParentCode": {
                                newCurrency.setCbIdP(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "Name": {
                                newCurrency.setName(currProp.getChildNodes().item(0).getNodeValue());
                            } break;
                            case "Nominal": {
                                newCurrency.setNominal(Integer.parseInt(currProp.getChildNodes()
                                        .item(0).getNodeValue()));
                            } break;
                        }
                    }
                }
                listCurrency.add(newCurrency);
            }
        }
        for(int i = 0; i < listCurrency.size(); i++) {
            System.out.println(listCurrency.get(i).getCbId());
            System.out.println(listCurrency.get(i).getNumCode());
            System.out.println(listCurrency.get(i).getCharCode());
            System.out.println(listCurrency.get(i).getName());
            System.out.println(listCurrency.get(i).getValue());

            System.out.println("===========>>>>");
        }
    }*/
}
