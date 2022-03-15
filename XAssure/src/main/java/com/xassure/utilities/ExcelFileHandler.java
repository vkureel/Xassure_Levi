package com.xassure.utilities;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileHandler {
    public String readExcel(String filePath, String fileName, String sheetName, String keyColName, String keyValue,
                            String columnName) {
        String excelData = null;

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);
            String strQuery = "Select * from " + sheetName;
            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {
                if (recordset.getField(keyColName).equals(keyValue)) {
                    excelData = recordset.getField(columnName);

                    break;
                }
            }

            recordset.close();
            connection.close();
        } catch (FilloException e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while fetching details from excels",
                    e);
        }

        return excelData;
    }

    public Map<String, String> readExcel(String filePath, String fileName, String sheetName, String keyColName,
                                         String keyValue) {
        Map<String, String> colValMap = new HashMap<>();

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);
            String strQuery = "Select * from " + sheetName;

            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {
                if (recordset.getField(keyColName).equals(keyValue)) {
                    List<String> columns = recordset.getFieldNames();
                    for (String col : columns) {
                        if (!col.equalsIgnoreCase(keyColName)) {
                            colValMap.put(col, recordset.getField(col));
                        }
                    }

                    break;
                }
            }
            recordset.close();
            connection.close();
        } catch (FilloException e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while fetching details from excels",
                    e);
        }

        return colValMap;
    }

    public List<Map<String, String>> readExcelMultipleColValues(String filePath, String fileName, String sheetName,
                                                                String testCaseName) {
        List<Map<String, String>> colMap = new ArrayList<>();

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);
            String strQuery = "Select * from " + sheetName + " where TestCaseName='" + testCaseName + "'";
            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {

                List<String> fieldNames = recordset.getFieldNames();
                Map<String, String> colValMap = new HashMap<>();
                for (String col : fieldNames) {

                    if (!col.contains("TestCaseName")) {
                        colValMap.put(col, recordset.getField(col));
                    }
                }

                colMap.add(colValMap);
            }

            recordset.close();
            connection.close();
        } catch (FilloException e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while fetching details from excels",
                    e);
        }

        return colMap;
    }

    public Map<String, Map<String, String>> readCompleteExcel(String filePath, String fileName, String sheetName,
                                                              String keyColName) {
        Map<String, Map<String, String>> colMap = new HashMap<>();

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);
            String strQuery = "Select * from " + sheetName;
            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {

                List<String> fieldNames = recordset.getFieldNames();
                Map<String, String> colValMap = new HashMap<>();
                for (String col : fieldNames) {

                    if (!col.contains(keyColName)) {
                        colValMap.put(col, recordset.getField(col));
                    }
                }

                colMap.put(recordset.getField(keyColName), colValMap);
            }

            recordset.close();
            connection.close();
        } catch (FilloException e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while fetching details from excels",
                    e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return colMap;
    }

    public Map<String, String> readExcelMultipleColValues(String filePath, String fileName, String sheetName,
                                                          String Key_Col1, String Val_Col2) {
        Map<String, String> colValuesMap = new HashMap<>();

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);
            String strQuery = "Select * from " + sheetName;
            Recordset recordset = connection.executeQuery(strQuery);
            List<String> fieldNames = recordset.getFieldNames();
            if (fieldNames.contains(Key_Col1)) {
                while (recordset.next()) {
                    colValuesMap.put(recordset.getField(Key_Col1), recordset.getField(Val_Col2));
                }
            } else {

                System.out.println("Please verify the column Names:-");
            }

            recordset.close();
            connection.close();
        } catch (FilloException e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while fetching details from excels",
                    e);
        }

        return colValuesMap;
    }

    public List<String> readSingleColumn(String filePath, String fileName, String sheetName, String Key_Col1) {
        List<String> columnValueList = new ArrayList<>();

        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);
            String strQuery = "Select " + Key_Col1 + " from " + sheetName;
            Recordset recordset = connection.executeQuery(strQuery);

            while (recordset.next()) {
                columnValueList.add(recordset.getField(Key_Col1));
            }

            recordset.close();
            connection.close();
        } catch (FilloException e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while fetching details from excels",
                    e);
        }

        return columnValueList;
    }

    public int insertRows(String filePath, String fileName, String sheetName, List<Map<String, String>> rows) {
        int rowsInserted = 0;
        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "/" + fileName);

            for (Map<String, String> rowSet : rows) {
                String col = "";
                String row = "";
                for (String key : rowSet.keySet()) {
                    if (col.isEmpty()) {
                        col = key;
                    } else {
                        col = col + "," + key;
                    }

                    if (row.isEmpty()) {
                        row = "'" + rowSet.get(key) + "'";
                        continue;
                    }
                    row = row + ",'" + rowSet.get(key) + "'";
                }

                String insertQuery = "INSERT INTO " + sheetName + "(" + col + ") VALUES(" + row + ")";
                connection.executeUpdate(insertQuery);
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsInserted;
    }

    public void deleteAll(String filePath, String filName, String sheetName) {
        try {
            Fillo fillo = new Fillo();

            Connection connection = fillo.getConnection(filePath + "/" + filName);

            String strQuery = "DELETE FROM " + sheetName;
            connection.executeUpdate(strQuery);

            connection.close();
        } catch (FilloException e) {
            e.printStackTrace();
        }
    }

    public void deleteColumnsData(String filePath, String filName, String sheetName, List<String> columns) {
        try {
            Fillo fillo = new Fillo();

            Connection connection = fillo.getConnection(filePath + "/" + filName);

            String updateToNullQuery = "";
            for (String column : columns) {
                if (updateToNullQuery.isEmpty()) {
                    updateToNullQuery = column + "=''";
                    continue;
                }
                updateToNullQuery = updateToNullQuery + "," + column + "=''";
            }

            String query = "DELETE FROM " + sheetName;

            connection.executeUpdate(query);

            connection.close();
        } catch (FilloException e) {
            e.printStackTrace();
        }
    }

    public void update(String filePath, String filName, String sheetName, Map<String, String> colValMap,
                       Map<String, String> whereClauseMap) {
        try {
            Fillo fillo = new Fillo();

            Connection connection = fillo.getConnection(filePath + "/" + filName);
            System.out.println();
            String updateColString = "";
            for (String col : colValMap.keySet()) {
                if (updateColString.isEmpty()) {
                    updateColString = col + "='" + colValMap.get(col) + "'";
                    continue;
                }
                updateColString = updateColString + "," + col + "='" + colValMap.get(col) + "'";
            }

            String whereClauseString = "";
            for (String whereCol : whereClauseMap.keySet()) {
                if (whereClauseString.isEmpty()) {
                    whereClauseString = whereCol + "='" + whereClauseMap.get(whereCol) + "'";
                    continue;
                }
                whereClauseString = whereClauseString + " and " + whereCol + "='"
                        + whereClauseMap.get(whereCol) + "'";
            }

            String query = "Update " + sheetName + " set " + updateColString + " where " + whereClauseString;
            connection.executeUpdate(query);
        } catch (FilloException e) {
            e.printStackTrace();
        }
    }
}
