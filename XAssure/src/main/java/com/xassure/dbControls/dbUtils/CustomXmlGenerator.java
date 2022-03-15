package com.xassure.dbControls.dbUtils;

import com.xassure.dbControls.manageDatabase.MetaDataTablesMap;
import com.xassure.dbControls.manageDatabase.ReadDatabases;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class CustomXmlGenerator {
    DocumentBuilderFactory documentBuilderFactory;
    DocumentBuilder documentBuilder;
    Document document;

    private static final String db_Mysql = "mysql";
    private static final String db_sqlServer = "sqlserver";
    private static final String db_oracle = "oracle";

    /**
     * Custom XML creation :-
     *
     * @param dbTableNames
     */
    public void customXMLCreation(String dbName, String dbUrl, String dbUserName, String dbPassword,
                                  List<String> dbTableNames, String pojoLocation) {

        try {
            // Creating an empty XML Document

            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();

            // DOMImplementation domImpl = document.getImplementation();
            // DocumentType doctype =
            // domImpl.createDocumentType("hibernate-configuration",
            // "-//Hibernate/Hibernate Configuration DTD 3.0//EN",
            // "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
            //
            // document.appendChild(doctype);
            // document.appendChild(document.createElement("hibernate-configuration"));

            // Creating XML tree
            // create the root element and add it to the document

            Element hibernateConfiguration = document.createElement("hibernate-configuration");
            document.appendChild(hibernateConfiguration);

            Element sessionFactory = document.createElement("session-factory");

            hibernateConfiguration.appendChild(sessionFactory);

            // Add the driver class for the session Factory:-
            Element driver_Class = document.createElement("property");
            driver_Class.setAttribute("name", "hibernate.connection.driver_class");

            if (dbName.contains(db_Mysql)) {

                sessionFactory.appendChild(driver_Class).appendChild(document.createTextNode("com.mysql.jdbc.Driver"));
            } else if (dbName.contains(db_sqlServer)) {

                sessionFactory.appendChild(driver_Class)
                        .appendChild(document.createTextNode("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
            } else if (dbName.contains(db_oracle)) {
                sessionFactory.appendChild(driver_Class)
                        .appendChild(document.createTextNode("oracle.jdbc.driver.OracleDriver"));
            }

            // Add the connection URL:-
            Element connection_URL = document.createElement("property");
            connection_URL.setAttribute("name", "hibernate.connection.url");

            // sessionFactory.appendChild(connection_URL)
            // .appendChild(document.createTextNode("jdbc:sqlserver://localhost:1433;databaseName=Testing"));

            sessionFactory.appendChild(connection_URL).appendChild(document.createTextNode(dbUrl));

            // Add the connection userName:-
            Element connection_Username = document.createElement("property");
            connection_Username.setAttribute("name", "hibernate.connection.username");

            // sessionFactory.appendChild(connection_Username).appendChild(document.createTextNode("sa"));
            sessionFactory.appendChild(connection_Username).appendChild(document.createTextNode(dbUserName));
            // Add the connection userName:-
            Element connection_Password = document.createElement("property");
            connection_Password.setAttribute("name", "hibernate.connection.password");

            // sessionFactory.appendChild(connection_Password).appendChild(document.createTextNode("info123!"));
            sessionFactory.appendChild(connection_Password).appendChild(document.createTextNode(dbPassword));
            // Add database dialect:-
            Element database_Dialect = document.createElement("property");
            database_Dialect.setAttribute("name", "hibernate.dialect");

            if (dbName.contains(db_Mysql)) {
                sessionFactory.appendChild(database_Dialect)
                        .appendChild(document.createTextNode("org.hibernate.dialect.MySQLDialect"));
            } else if (dbName.contains(db_sqlServer)) {
                sessionFactory.appendChild(database_Dialect)
                        .appendChild(document.createTextNode("org.hibernate.dialect.SQLServerDialect"));
            } else if (dbName.contains(db_oracle)) {
                sessionFactory.appendChild(database_Dialect)
                        .appendChild(document.createTextNode("org.hibernate.dialect.Oracle10gDialect"));
            }

            // Show database Query > Set as True:
            Element show_Query = document.createElement("property");
            show_Query.setAttribute("name", "show_sql");

            sessionFactory.appendChild(show_Query).appendChild(document.createTextNode("true"));

            // Add the mapping table nanmes:-

            for (String dbTableName : dbTableNames) {

                Element database_Table = document.createElement("mapping");
                database_Table.setAttribute("class", "com.xassure.dbTables." + dbTableName);

                sessionFactory.appendChild(database_Table);
            }

            // create XML file
            String current = System.getProperty("user.dir");
            File xmlFile = new File(pojoLocation + "/hibernate.cfg.xml");
            System.out.println("XML File Location " + xmlFile);
            xmlFile.createNewFile();
            FileOutputStream isod = new FileOutputStream(xmlFile);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMImplementation domImpl = document.getImplementation();

            DocumentType doctype = domImpl.createDocumentType("doctype",
                    "-//Hibernate/Hibernate Configuration DTD 3.0//EN",
                    "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(isod);
            transformer.transform(source, result);

            isod.flush();
            isod.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MetaDataTablesMap _dbTablesMap = new ReadDatabases().readDatabase("mysql",
                "jdbc:mysql://localhost:3306/classicmodels", "root", "info123!", null, null);
        List<String> tableNames = _dbTablesMap.getDbTableNames();

        new CustomXmlGenerator().customXMLCreation("mysql", "jdbc:mysql://localhost:3306/classicmodels", "root",
                "info123!", tableNames, "");

    }
}
