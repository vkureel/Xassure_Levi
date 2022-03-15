package com.xassure.dbControls.manageDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadDatabases {
    // Class variables:
    private DatabaseMetaData md;
    private Connection conn;

    private static final String driverMySQL = "com.mysql.jdbc.Driver";
    //private static final String	driverMySQL		= "com.mysql.cj.jdbc.Driver";
    private static final String driverSQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String driverOracle = "oracle.jdbc.driver.OracleDriver";
    private static final String db_Mysql = "mysql";
    private static final String db_sqlServer = "sqlserver";
    private static final String db_oracle = "oracle";

    /*
     *
     * Method to create the connection to the Database:-
     */
    private Connection getDBConnection(String DBName, String url, String userName, String password) {

        String driver = null;
        try {

            if (DBName.trim().toLowerCase().contains(db_Mysql)) {
                driver = driverMySQL;

            } else if (DBName.trim().toLowerCase().contains(db_sqlServer)) {
                driver = driverSQLSERVER;
            } else if (DBName.trim().toLowerCase().contains(db_oracle)) {
                driver = driverOracle;
            }

            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);

        } catch (ClassNotFoundException cl) {
            System.out.println("Class not found exception has occured please verify the database login details:-");
            cl.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("SQL exception has occured please verify the database login details:-");
            ex.printStackTrace();

        } catch (Exception e) {
            System.out.println("Exception has occured please verify the database login details:-");
            e.printStackTrace();
        }

        return conn;
    }

    /*
     * Method to get the Schemas of the database:-
     */
    private List<String> getSchemas() {
        List<String> schemaNames = new ArrayList<String>();
        String schemaName;
        ResultSet schemas;
        try {

            md = conn.getMetaData();
            schemas = md.getSchemas();

            while (schemas.next()) {

                //schemaName = schemas.getString("TABLE_CAT");
                schemaName = schemas.getString(1).trim().toLowerCase();
                schemaNames.add(schemaName);

            }

        } catch (Exception e) {
            System.out.println("Exception occured while fetching the schemas of the Database:-");
            e.printStackTrace();
        }
        return schemaNames;
    }

    private List<String> getTableNames(ResultSet dbTables, List<String> tableList) {

        List<String> dbTableNames = new ArrayList<String>();
        String tableName = "";

        try {
            if (tableList != null && !tableList.isEmpty()) {
                dbTableNames = tableList;
            } else {
                while (dbTables.next()) {

                    tableName = dbTables.getString(3);
                    dbTableNames.add(tableName);

                }
            }
        } catch (SQLException ex) {
            System.out.println("SQL exception has occured while reading the tables of database:-");
            ex.printStackTrace();

        }

        return dbTableNames;
    }

    /*
     * Set Table column properties Map:
     */
    private void setTableColumnPropertiesMap(String tableName, TableProperties tablePropertiesMap) {
        ResultSetMetaData resultSetMetaData;
        ResultSet executeQuery;
        Statement stmnt;
        TableColumnProperties tableColumnsPropertiesMap;
        // TableProperties tablePropertiesMap = new TableProperties();
        int totalColumnsCount = 0;
        Map<String, TableColumnProperties> columnPropertyMap = new HashMap<String, TableColumnProperties>();
        boolean nullableValue = false;
        boolean bFlag = true;
        try {

            stmnt = conn.createStatement();
            executeQuery = stmnt.executeQuery("select * from " + tableName);
            resultSetMetaData = executeQuery.getMetaData();

            // Fetch the total number of tables:-
            totalColumnsCount = getTableColumnsCount(resultSetMetaData);

            // Set the total Total count of columns in a table:-
            tablePropertiesMap.setTotalColumnCount(totalColumnsCount);

            for (int colCount = 1; colCount <= totalColumnsCount; colCount++) {
                tableColumnsPropertiesMap = new TableColumnProperties();
                /*
                 * Set the Column Name:-
                 */
                String columnName = resultSetMetaData.getColumnName(colCount);

                /*
                 * Set the Column Size:-
                 */
                tableColumnsPropertiesMap.setColumnSize(resultSetMetaData.getPrecision(colCount));

                /*
                 * set column null status:-
                 *
                 */
                if (resultSetMetaData.isNullable(colCount) == 1) {
                    nullableValue = true;
                } else if (resultSetMetaData.isNullable(colCount) == 0) {
                    nullableValue = false;
                }
                tableColumnsPropertiesMap.setColumnNullStatus(nullableValue);

                /*
                 * Get Column Type:-
                 *
                 */
                tableColumnsPropertiesMap.setColumnTypeName(resultSetMetaData.getColumnTypeName(colCount));

                /*
                 * Set the column property map:-
                 */
                columnPropertyMap.put(columnName, tableColumnsPropertiesMap);

                /*
                 * Reset the value of nullable:-
                 */
                nullableValue = false;
            }

            /*
             * Set columns Properties under table properties Object:-
             */
            tablePropertiesMap.setColumnProperties(columnPropertyMap);
            bFlag = false;

        } catch (SQLException e) {
            if (bFlag) {
                tableName = "[" + tableName + "]";
                setTableColumnPropertiesMap(tableName, tablePropertiesMap);
                bFlag = false;
            } else {
                System.out.println("Exception occured while fetching the properties of the table column:");
                e.printStackTrace();
            }

        } catch (Exception e) {

            System.out.println("Exception occured while reading column properties of the database:-");
            e.printStackTrace();
        }

    }

    /*
     *
     * Fetch the total Tables count:-
     */
    private int getTableColumnsCount(ResultSetMetaData resultSetMetaData) {

        int totalColumnsCount = -1;
        try {

            totalColumnsCount = resultSetMetaData.getColumnCount();
        } catch (SQLException e) {

        }

        return totalColumnsCount;
    }

    /*
     * Set the total count of Primary Keys of the table:-
     */
    private void setTablePrimaryKeys(String tableName, TableProperties tablePropertiesMap) {
        ResultSet p_Keys;
        List<String> tablePrimaryKeys = new ArrayList<String>();
        try {

            p_Keys = md.getPrimaryKeys(null, null, tableName.toUpperCase());

            while (p_Keys.next()) {
                tablePrimaryKeys.add(p_Keys.getString(4));

            }

            /*
             * Set the value of Primary Keys for the
             */
            tablePropertiesMap.setPrimaryKeys(tablePrimaryKeys);
        } catch (SQLException e) {
            System.out.println("Exception occured while fetching the primary keys of the table:-");
            e.printStackTrace();
        }
    }

    /*
     * Set the total count of Unique Keys of the table:-
     */
    private void setTableUniqueKeys(String tableName, TableProperties tablePropertiesMap) {
        ResultSet u_Keys;
        List<String> tableUniqueKeys = new ArrayList<String>();
        try {

            // p_Keys = md.getPrimaryKeys(null, null, tableName);
            u_Keys = md.getIndexInfo(null, null, tableName, true, true);
            while (u_Keys.next()) {
                if (!(u_Keys.getString(9) == null))
                    tableUniqueKeys.add(u_Keys.getString(9));

            }

            /*
             * Set the value of Primary Keys for the
             */
            tablePropertiesMap.setUniqueKeys(tableUniqueKeys);
        } catch (SQLException e) {
            System.out.println("Exception occured while fetching the primary keys of the table:-");
            e.printStackTrace();
        }
    }

    /*
     *
     * Set current Table ColumnName and Exported Column:Table Mapping:-
     */
    private void setCurrentTableAndExportedColumnTableMapping(String tableName, TableProperties tablePropertiesMap) {
        ResultSet currentTableColumnExportedTableMapping;
        Map<HashMap<String, String>, String> currentTableColumnAndExportedColumnTableMapping = new HashMap<HashMap<String, String>, String>();

        try {

            currentTableColumnExportedTableMapping = md.getExportedKeys(conn.getCatalog(), null, tableName);

            while (currentTableColumnExportedTableMapping.next()) {
                HashMap<String, String> exportedColumnTableMapping = new HashMap<String, String>();

                exportedColumnTableMapping.put(currentTableColumnExportedTableMapping.getString(8),
                        currentTableColumnExportedTableMapping.getString(7));

                currentTableColumnAndExportedColumnTableMapping.put(exportedColumnTableMapping,
                        currentTableColumnExportedTableMapping.getString(4));

            }
            tablePropertiesMap.setExportedColumnTableAndCurrentTableColumnMapping(currentTableColumnAndExportedColumnTableMapping);

        } catch (SQLException e) {
            System.out.println("Exception occured while fetching the primary keys of the table:-");
            e.printStackTrace();
        }
    }

    /*
     *
     * Set current Table ColumnName and Imported Column:Table Mapping:-
     */
    private void setCurrentTableAndImportedColumnTableMapping(String tableName, TableProperties tablePropertiesMap) {
        ResultSet currentTableColumnImportedTableMapping;
        Map<HashMap<String, String>, String> currentTableColumnAndImportedColumnTableMapping = new HashMap<HashMap<String, String>, String>();

        try {

            currentTableColumnImportedTableMapping = md.getImportedKeys(conn.getCatalog(), null, tableName);

            while (currentTableColumnImportedTableMapping.next()) {
                HashMap<String, String> exportedColumnTableMapping = new HashMap<String, String>();

                exportedColumnTableMapping.put(currentTableColumnImportedTableMapping.getString(4),
                        currentTableColumnImportedTableMapping.getString(3));

                currentTableColumnAndImportedColumnTableMapping.put(exportedColumnTableMapping,
                        currentTableColumnImportedTableMapping.getString(8));

            }
            tablePropertiesMap.setImportedColumnTableAndCurrentTableColumnMapping(currentTableColumnAndImportedColumnTableMapping);

        } catch (SQLException e) {
            System.out.println("Exception occured while fetching the primary keys of the table:-");
            e.printStackTrace();
        }
    }

    /*
     * Method to read the databse:
     */
    public MetaDataTablesMap readDatabase(String DBName, String url, String userName, String password, String schemaName, List<String> tableList) {
        String dbSchemaName = null;
        List<String> dbSchemaNames = null;
        ResultSet dbTables;
        Map<String, TableProperties> dbTablePropertiesMapping = new HashMap<String, TableProperties>();
        Map<String, MetaDataTablesMap> databaseTablesMapping = new HashMap<String, MetaDataTablesMap>();
        MetaDataTablesMap DatabaseTablesMap = new MetaDataTablesMap();
        List<String> dbTableNames;

        try {
            conn = getDBConnection(DBName, url, userName, password);
            md = conn.getMetaData();

            // Fetch the Schema Name:-

            dbSchemaNames = getSchemas();
            if (DBName.trim().toLowerCase().contains(db_oracle)) {
                if (!(dbSchemaNames == null)) {
                    for (String schema : dbSchemaNames) {
                        if (schema.equalsIgnoreCase(schemaName)) {
                            dbSchemaName = schemaName;
                        }

                    }
                }

            } else {
                if (!(dbSchemaNames == null)) {
                    if (dbSchemaNames.contains(schemaName)) {

                        dbSchemaName = schemaName;
                    }

                }
            }

            dbTables = md.getTables(conn.getCatalog(), dbSchemaName, "%", null);

            // Fetch tables of the Database:-
            dbTableNames = getTableNames(dbTables, tableList);

            // Set the Meta data and Table mapping:-

            DatabaseTablesMap.setDbTableNames(dbTableNames);

            databaseTablesMapping.put(DBName, DatabaseTablesMap);

            for (String tableName : dbTableNames) {
                TableProperties tablePropertiesMap = new TableProperties();
                //tableName = dbSchemaName+"."+tableName;

                /*
                 * Set the table Columns Properties:-
                 */
                setTableColumnPropertiesMap(tableName, tablePropertiesMap);

                /*
                 * Set the table Primary Keys:-
                 */
                setTablePrimaryKeys(tableName, tablePropertiesMap);

                /*
                 * Set table Unique keys:-
                 */
                setTableUniqueKeys(tableName, tablePropertiesMap);

                /*
                 * Set current table and Exported column:Table Mapping:-
                 */
                setCurrentTableAndExportedColumnTableMapping(tableName, tablePropertiesMap);

                /*
                 * Set current table and Imported column:Table Mapping:-
                 */
                setCurrentTableAndImportedColumnTableMapping(tableName, tablePropertiesMap);

                /*
                 * Set the Table Properties mapping:-
                 */
                dbTablePropertiesMapping.put(tableName, tablePropertiesMap);

            }

            DatabaseTablesMap.setDbTablePropertiesMap(dbTablePropertiesMapping);
        } catch (SQLException e) {
            System.out.println("Exception occured while reading the table Properties");
            e.printStackTrace();
        }

        return DatabaseTablesMap;
    }
	
	
/*public static void main(String[] args) throws RemoteException {
		
		DbControlsLibrary _controls = new DbControlsLibrary();
		List<employees> employeeObjList=_controls.readAllDataFromTable("employees");
		for(employees emp : employeeObjList)
		System.out.println(emp.getfirstName());
		Map<String, String> inputDataMap = new ExcelReader().readExcel("sanity", "PGS_WS_DemoTCs.xlsx",
				"PGS_WS_CPT", "Test_Name", "OPS_ChasePT_Refund_002");
		
	}*/

}
