package org.ampathkenya.utils.sqlgenerator;

import com.sun.deploy.util.Property;
import org.ampathkenya.utils.configparser.TableConfig;

import java.util.Enumeration;
import java.util.Properties;

public class SQLGenerator {

    private String csvFileContent ;
    private TableConfig csvTableConfig ;
    private final String FOREIGN_KEY = "foreignkey" ;
    private final String PRIMARY_KEY = "primarykey" ;
    private final String SQL_HEADER = "-- MySQL dump 10.13  Distrib 5.5.34, for debian-linux-gnu (i686)\n" +
            "--\n" +
            "-- Host: localhost    Database: test\n" +
            "-- ------------------------------------------------------\n" +
            "-- Server version       5.5.34-0ubuntu0.13.04.1\n" +
            "\n" +
            "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n" +
            "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n" +
            "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n" +
            "/*!40101 SET NAMES utf8 */;\n" +
            "/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n" +
            "/*!40103 SET TIME_ZONE='+00:00' */;\n" +
            "/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;\n" +
            "/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n" +
            "/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n" +
            "/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n" +
            "\n"  ;
    private final String SQL_FOOTER = "/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;\n" +
            "/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;\n" +
            "/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;\n" +
            "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;\n" +
            "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;\n" +
            "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;\n" +
            "/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;\n" ;

    public SQLGenerator(String csvFileContent, TableConfig csvTableConfig) {
        this.csvFileContent = csvFileContent ;
        this.csvTableConfig = csvTableConfig ;
    }

    public String displayTableConfig() {
        String displayedConfigs  = "" ;
        String tableName = csvTableConfig.getTableName() ;
        String tableConfigs = obtainStringTableConfigs(csvTableConfig) ;

        if(csvTableConfig != null) {
            displayedConfigs += "Converting " +
                    tableName +
                    " with configs\n" +
                    tableConfigs ;
        }

        return displayedConfigs ;
    }

    public String generateSQL() {
        String generatedSQL = ""  ;

        generatedSQL += SQL_HEADER ;
        generatedSQL += createDDLStatements(csvTableConfig) ;
        generatedSQL += createDMLStatements(csvFileContent, csvTableConfig)  ;
        generatedSQL += SQL_FOOTER ;

        return generatedSQL ;
    }

    private String obtainStringTableConfigs (TableConfig csvTableConfig) {
        String tableConfigString  = "";

        Properties tableConfigs = csvTableConfig.getTableConfigProperties() ;
        Enumeration tableConfigKeys = tableConfigs.keys() ;

        while (tableConfigKeys.hasMoreElements()) {
            String key = (String) tableConfigKeys.nextElement() ;
            String value = (String) tableConfigs.get(key) ;

            tableConfigString += key + "=" + value + "\n" ;
        }

        return tableConfigString ;
    }

    private String createDDLStatements(TableConfig csvTableConfig) {
        String DDLStatement = "" ;

        DDLStatement += "--\n" +
                "-- Table structure for table `" +
                csvTableConfig.getTableName() +
                "`\n" + "--\n" ;

        DDLStatement += "/*!40101 SET @saved_cs_client     = @@character_set_client */;\n" +
                "/*!40101 SET character_set_client = utf8 */;\n" +
                "CREATE TABLE `" +
                csvTableConfig.getTableName() +
                "` (\n" ;

        Properties tableConfigs = csvTableConfig.getTableConfigProperties() ;
        Enumeration tableConfigsKeys =  tableConfigs.keys() ;
        int tableConfigsCounter = 0 ;
        boolean isLast = false ;

        while(tableConfigsKeys.hasMoreElements()) {
            String key = (String) tableConfigsKeys.nextElement() ;
            String value = (String) tableConfigs.get(key) ;

            if(tableConfigsCounter == tableConfigs.size())
                isLast = true ;

            DDLStatement += processKeyAndValue(key, value, isLast) ;
        }

        DDLStatement += ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +
                "/*!40101 SET character_set_client = @saved_cs_client */;\n" ;

        return  DDLStatement ;
    }

    private String processKeyAndValue(String key, String value, boolean isLast) {
        String returnedString = "" ;

        if (key.equals(PRIMARY_KEY)) {
            returnedString += "PRIMARY KEY (`" +
                    value +
                    "`)\n" ;
        } else if (key.equals(FOREIGN_KEY)) {
            returnedString += "FOREIGN KEY " +
                    value + "\n";
        } else {
            if(!isLast)
                returnedString += "`" + key + "` " + value + ",\n";
            else
                returnedString += "`" + key + "` " + value + "\n";
        }

        return returnedString ;
    }

    private String createDMLStatements(String csvFileContent, TableConfig tableConfig) {

        String DMLString = "--\n" +
                "-- Dumping data for table `" +
                tableConfig.getTableName() +
                "`\n" +
                "--\n" +
                "\n" +
                "LOCK TABLES `test1` WRITE;\n" +
                "/*!40000 ALTER TABLE `" +
                tableConfig.getTableName() +
                "` DISABLE KEYS */;\n" ;

        String[] csvLines = splitCSVFileToLines(csvFileContent) ;

        for (String csvLine : csvLines ) {
            DMLString += "INSERT INTO " +
                    tableConfig.getTableName() +
                    " VALUES (" +
                    csvLine +
                    ");\n" ;
        }

        DMLString += "/*!40000 ALTER TABLE `" +
                csvTableConfig.getTableName() +
                "` ENABLE KEYS */;\n" +
                "UNLOCK TABLES;\n" +
                "/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;\n" ;

        return DMLString ;
    }

    private String[] splitCSVFileToLines(String csvFileContent) {
        return csvFileContent.split("\n") ;
    }
}
