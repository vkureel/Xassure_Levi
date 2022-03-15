package com.xassure.dbControls.dbUtils;

import com.sun.codemodel.*;
import com.xassure.dbControls.manageDatabase.MetaDataTablesMap;
import com.xassure.dbControls.manageDatabase.ReadDatabases;
import com.xassure.dbControls.manageDatabase.TableColumnProperties;
import com.xassure.dbControls.manageDatabase.TableProperties;
import com.xassure.reporting.utilities.PropertiesFileHandler;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class CreatePojo {

    private static JCodeModel jCodeModel;
    private static JDefinedClass definedClass;

    private Map<String, JDefinedClass> referenceClass = new HashMap<String, JDefinedClass>();
    private Map<String, JDefinedClass> embeddedClass = new HashMap<String, JDefinedClass>();

    // Database Variables:-
    private static String DB_TYPE;
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static String DB_SCHEMANAME;
    private static String DB_POJOLOCATION;

    private Map<String, String> selfJoinColumnTableMapping = new HashMap<String, String>();
    private List<String> currentTableManyToOneColMap = new ArrayList<String>();

    public static final String MAPPING_ONE_2_ONE = "one2One";
    public static final String MAPPING_ONE_2_MANY = "one2Many";
    public static final String MAPPING_MANY_2_MANY = "Many2Many";
    public static final String MAPPING_MANY_2_ONE = "many2One";

    public CreatePojo() {
        PropertiesFileHandler _properties = new PropertiesFileHandler();
        DB_TYPE = _properties.readProperty("dbConnection", "DB_TYPE");
        DB_URL = _properties.readProperty("dbConnection", "DB_URL");
        DB_USERNAME = _properties.readProperty("dbConnection", "DB_USERNAME");
        DB_PASSWORD = _properties.readProperty("dbConnection", "DB_PASSWORD");
        DB_SCHEMANAME = _properties.readProperty("dbConnection", "DB_SCHEMANAME");
        DB_POJOLOCATION = _properties.readProperty("dbConnection", "DB_POJOLOCATION");
    }

    public CreatePojo(String DBName, String url, String userName, String password, String schemaName,
                      String pojoLocation) {
        DB_TYPE = DBName;
        DB_URL = url;
        DB_USERNAME = userName;
        DB_PASSWORD = password;
        DB_SCHEMANAME = schemaName;
        DB_POJOLOCATION = pojoLocation;
    }

    /**
     * Method to create class heading annotation as per the table name
     *
     * @param tableName
     */
    private void addClassHeadingAnnotation(String tableName) {
        try {
            definedClass.annotate(javax.persistence.Entity.class);
            definedClass.annotate(javax.persistence.Table.class).param("name", tableName);

        } catch (Exception e) {

            System.out.println("Exception occured while creating the table name");
            e.printStackTrace();
        }
    }

    /**
     * Method to create class heading annotation as per the table name for
     * Embedded Class:-
     *
     * @param tableName
     */
    private void addClassHeadingAnnotationEmbeddedClass(String tableName) {
        try {
            JDefinedClass defClass = embeddedClass.get(tableName + "Id");
            defClass.annotate(javax.persistence.Embeddable.class);
        } catch (Exception e) {
            System.out.println("Exception occured while creating the table name");
            e.printStackTrace();
        }
    }

    /**
     * Create default constructor of the class:-
     *
     * @param jClass
     */
    private void createDefaultConstructor(JDefinedClass jClass) {

        try {
            jClass.constructor(1);

        } catch (Exception e) {

            System.out.println("Exception occured while creating the default constructor of the class");
            e.printStackTrace();
        }
    }

    /**
     * Method to create field variables:-
     *
     * @param fieldVariables
     */
    @SuppressWarnings("unused")
    public void createPrivateFieldVariables(TableProperties tableProperties,
                                            HashMap<String, String> tableMappingAnnotations, String tableName) {

        Map<String, TableColumnProperties> tableColumnProperties;
        TableColumnProperties columnProperties;
        try {

            tableColumnProperties = tableProperties.getColumnProperties();
            Map<HashMap<String, String>, String> exportedTableColumnAndCurrentColumnMap = tableProperties
                    .getExportedColumnTableAndCurrentTableColumnMapping();
            Map<HashMap<String, String>, String> importedTableColumnAndCurrentColumnMap = tableProperties
                    .getImportedColumnTableAndCurrentTableColumnMapping();
            List<String> table_PrimaryKeys = tableProperties.getPrimaryKeys();

            /*
             * Create Class variables:-
             */
            for (Entry<String, TableColumnProperties> tableColumns : tableColumnProperties.entrySet()) {

                columnProperties = tableColumns.getValue();
                String coLPropertyType = columnProperties.getColumnTypeName();
                String columnName = tableColumns.getKey();

                if (!selfJoinColumnTableMapping.containsKey(columnName)) {
                    if (table_PrimaryKeys.size() > 1) {
                        if (!table_PrimaryKeys.contains(columnName))

                            if (!currentTableManyToOneColMap.contains(columnName))
                                definedClass.field(JMod.PRIVATE, getVariableType(coLPropertyType), columnName)
                                        .assign(JExpr.lit(2));
                    } else {
                        if (!currentTableManyToOneColMap.contains(columnName))
                            definedClass.field(JMod.PRIVATE, getVariableType(coLPropertyType), columnName)
                                    .assign(JExpr.lit(2));
                    }

                } else {
                    definedClass.field(JMod.PRIVATE, referenceClass.get(tableName), "sup" + columnName);

                    JClass rawArrayListClass = jCodeModel.ref(HashSet.class);
                    JClass arrayListFieldClazz = rawArrayListClass.narrow(referenceClass.get(tableName));

                    JExpression newInstance = JExpr._new(arrayListFieldClazz).arg(JExpr.lit(0));

                    JFieldVar jfv = definedClass.field(JMod.PRIVATE,
                            jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableName)),
                            "sub" + columnName);

                    jfv.init(newInstance);
                }

            }

            /*
             * Create Id for the class variables for the primary Keys (condtn:
             * composite key:)
             */

            if (tableProperties.getPrimaryKeys().size() > 1) {

                // Create reference of the Composite Id class:-

                definedClass.field(JMod.PRIVATE, embeddedClass.get(tableName + "Id"), "_" + tableName + "Id")
                        .assign(JExpr.lit(2));

            }

            /*
             * Add References of the Class if required:-
             */

            String tableNameField = "";
            String simpleClassName = "";
            // JDefinedClass runtimeClass;

            for (Entry<String, String> tableMapping : tableMappingAnnotations.entrySet()) {

                tableNameField = tableMapping.getKey();
                simpleClassName = "_" + tableNameField;

                if (tableMapping.getValue().contains(MAPPING_ONE_2_ONE)) {
                    definedClass.field(JMod.PRIVATE, referenceClass.get(tableNameField), simpleClassName);
                }
                if (tableMapping.getValue().contains(MAPPING_ONE_2_MANY)) {
                    JClass rawArrayListClass = jCodeModel.ref(HashSet.class);

                    JClass arrayListFieldClazz = rawArrayListClass.narrow(referenceClass.get(tableNameField));

                    JExpression newInstance = JExpr._new(arrayListFieldClazz).arg(JExpr.lit(0));
                    JFieldVar jfv = definedClass.field(JMod.PRIVATE,
                            jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableNameField)),
                            simpleClassName);
                    jfv.init(newInstance);
                }
                if (tableMapping.getValue().contains(MAPPING_MANY_2_ONE)) {

                    definedClass.field(JMod.PRIVATE, referenceClass.get(tableNameField), simpleClassName);

                }

            }

        } catch (Exception e) {

            System.out.println("Exception occured while creating private variable of the class");
            e.printStackTrace();
        }
    }

    /**
     * Fetch class type of the database column name:-
     *
     * @param dbPropertyTypeName
     * @return
     */
    public Class<?> getVariableType(String dbPropertyTypeName) {

        Class<?> classType = null;

        try {

            if (dbPropertyTypeName.trim().toLowerCase().contains("varchar")) {

                classType = String.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("int")) {

                classType = int.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("tinyint")) {

                classType = int.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("smallint")) {

                classType = int.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("mediumint")) {

                classType = int.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("bigint ")) {

                classType = Long.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("double")) {

                classType = double.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("float")) {

                classType = float.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("decimal ")) {

                classType = float.class;
            } else if (dbPropertyTypeName.trim().toLowerCase().contains("date")) {
                classType = Date.class;
            } else {

                classType = String.class;
            }

        } catch (Exception e) {

            System.out.println("Exception occured while fetching the variable type");
            e.printStackTrace();

        }

        return classType;
    }

    /**
     * Create Table Mapping:-
     */

    public HashMap<String, String> creatDBTableMapping(MetaDataTablesMap DatabaseTablesMap,
                                                       TableProperties tableProperties, String tableName) {
        Map<HashMap<String, String>, String> currentTableColumnAndExportedColumnTableNameMap = tableProperties
                .getExportedColumnTableAndCurrentTableColumnMapping();

        Map<HashMap<String, String>, String> currentTableNameAndImportedColumnTableNameMap = tableProperties
                .getImportedColumnTableAndCurrentTableColumnMapping();

        Map<String, TableProperties> tablePropertiesMap = DatabaseTablesMap.getDbTablePropertiesMap();

        List<String> tablePrimarykeys = tableProperties.getPrimaryKeys();

        HashMap<String, String> tableAnnotationMapping = new HashMap<String, String>();

        // Fetch the Unique keys of the table:-
        List<String> currentTableUniquekeys = tableProperties.getUniqueKeys();

        List<String> uniqueKeyColumns = new ArrayList<String>();
        for (String uniqueKey : currentTableUniquekeys) {
            if (tablePrimarykeys.contains(uniqueKey) == false) {
                uniqueKeyColumns.add(uniqueKey);
            }
        }

        /*
         * Iterate over to calculate the Imported Tables Count:-
         */
        for (Entry<HashMap<String, String>, String> importedColumnsTableMap : currentTableNameAndImportedColumnTableNameMap
                .entrySet()) {
            String currentTableColumnValue = importedColumnsTableMap.getValue();
            HashMap<String, String> importedColumnTableMap = importedColumnsTableMap.getKey();

            for (Entry<String, String> importedColumnAndTableMap : importedColumnTableMap.entrySet()) {
                TableProperties importedTableProperties;
                String importedTableName = importedColumnAndTableMap.getValue();

                importedTableProperties = tablePropertiesMap.get(importedTableName);
                List<String> importedTablePrimaryKeys = importedTableProperties.getPrimaryKeys();
                if (!importedTableName.toLowerCase().equalsIgnoreCase(tableName)) {

                    if (importedTablePrimaryKeys.size() == 1) {
                        if (tablePrimarykeys.contains(currentTableColumnValue) && tablePrimarykeys.size() == 1) {

                            tableAnnotationMapping.put(importedTableName, MAPPING_ONE_2_ONE);
                        } else if (tablePrimarykeys.contains(currentTableColumnValue) && tablePrimarykeys.size() > 1) {

                            currentTableManyToOneColMap.add(currentTableColumnValue);
                            tableAnnotationMapping.put(importedTableName, MAPPING_MANY_2_ONE);
                        } else if (tablePrimarykeys.contains(currentTableColumnValue) == false
                                && uniqueKeyColumns.contains(currentTableColumnValue)) {
                            tableAnnotationMapping.put(importedTableName, MAPPING_ONE_2_ONE);
                        } else if ((tablePrimarykeys.contains(currentTableColumnValue) == false)
                                && (uniqueKeyColumns.contains(currentTableColumnValue) == false)) {
                            currentTableManyToOneColMap.add(currentTableColumnValue);
                            tableAnnotationMapping.put(importedTableName, MAPPING_MANY_2_ONE);
                        }

                    } else if (importedTablePrimaryKeys.size() != 1) {
                        if (tablePrimarykeys.contains(currentTableColumnValue) && tablePrimarykeys.size() == 1) {
                            tableAnnotationMapping.put(importedTableName, MAPPING_ONE_2_MANY);
                        }
                    }
                } else {

                    selfJoinColumnTableMapping.put(currentTableColumnValue, importedTableName);
                }

            }
        }

        /*
         * Iterate over to calculate the Exported Tables:-
         */
        for (Entry<HashMap<String, String>, String> exportedTablesCount : currentTableColumnAndExportedColumnTableNameMap
                .entrySet()) {

            HashMap<String, String> exportedColumnTableMap = exportedTablesCount.getKey();
            String currentTableColumnName = exportedTablesCount.getValue();

            // Iterate over the exported column and Table Mapping:-
            for (Entry<String, String> exportedTablesColumnMap : exportedColumnTableMap.entrySet()) {
                TableProperties exportedTableProperties;
                String exportedTableName = exportedTablesColumnMap.getValue();
                String exportedTableColumnName = exportedTablesColumnMap.getKey();

                // Fetch the exported Table properties:-
                exportedTableProperties = tablePropertiesMap.get(exportedTableName);

                List<String> exprtedTableUniqueKeys = getUniqueKeys(exportedTableProperties);
                List<String> exportedTablePrimarykeys = exportedTableProperties.getPrimaryKeys();

                if (!exportedTableName.toLowerCase().equalsIgnoreCase(tableName)) {

                    if (((exprtedTableUniqueKeys.contains(exportedTableColumnName)
                            && exprtedTableUniqueKeys.size() == 1)
                            || exportedTablePrimarykeys.contains(exportedTableColumnName)
                            && exportedTablePrimarykeys.size() == 1)
                            && tablePrimarykeys.size() == 1) {
                        tableAnnotationMapping.put(exportedTableName, MAPPING_ONE_2_ONE);
                    } else if (((exprtedTableUniqueKeys.contains(exportedTableColumnName)
                            || exportedTablePrimarykeys.contains(exportedTableColumnName)) == false)
                            && tablePrimarykeys.size() == 1) {
                        tableAnnotationMapping.put(exportedTableName, MAPPING_ONE_2_MANY);
                    } else if (((exprtedTableUniqueKeys.contains(exportedTableColumnName)
                            && exprtedTableUniqueKeys.size() == 1)
                            || (exportedTablePrimarykeys.contains(exportedTableColumnName)
                            && exportedTablePrimarykeys.size() == 1))
                            && tablePrimarykeys.size() != 1) {
                        currentTableManyToOneColMap.add(currentTableColumnName);
                        tableAnnotationMapping.put(exportedTableName, MAPPING_MANY_2_ONE);
                    } else if (((exprtedTableUniqueKeys.contains(exportedTableColumnName)
                            && exprtedTableUniqueKeys.size() != 1)
                            || (exportedTablePrimarykeys.contains(exportedTableColumnName)
                            && exportedTablePrimarykeys.size() != 1))
                            && tablePrimarykeys.size() == 1) {
                        tableAnnotationMapping.put(exportedTableName, MAPPING_ONE_2_MANY);
                    }
                } else {
                    if (!selfJoinColumnTableMapping.containsKey(exportedTableColumnName)) {

                        selfJoinColumnTableMapping.put(exportedTableColumnName, exportedTableName);
                    }
                }

            }

        }

        return tableAnnotationMapping;

    }

    /**
     * Method to fetch the unique keys of the table:-
     */
    public List<String> getUniqueKeys(TableProperties tableProperties) {

        List<String> uniqueKeyColumns = new ArrayList<String>();
        List<String> uniqueKeysColIncldingPrimaryKeys = tableProperties.getUniqueKeys();
        List<String> tablePrimarykeys = tableProperties.getPrimaryKeys();

        for (String uniqueKey : uniqueKeysColIncldingPrimaryKeys) {
            if (tablePrimarykeys.contains(uniqueKey) == false) {
                uniqueKeyColumns.add(uniqueKey);
            }
        }

        return uniqueKeyColumns;

    }

    /**
     * Method to create Parameterized constructor:-
     */
    @SuppressWarnings("unused")
    private void createParametrizedConstructor(TableProperties tableProperties,
                                               HashMap<String, String> tableMappingAnnotations) {

        Map<String, TableColumnProperties> _tableColumnPropertiesMap;
        List<String> tablePrimaryKeys = tableProperties.getPrimaryKeys();

        try {
            JMethod paramConstructor = definedClass.constructor(JMod.PUBLIC);
            String tableNameField = "";
            String simpleClassName = "";

            _tableColumnPropertiesMap = tableProperties.getColumnProperties();
            /* Used to parameterize the constructor:- */
            for (Entry<String, TableColumnProperties> _columnProperties : _tableColumnPropertiesMap.entrySet()) {
                TableColumnProperties tableColumnProperties;
                String columnName = _columnProperties.getKey();
                tableColumnProperties = _columnProperties.getValue();

                if (!(tablePrimaryKeys.contains(columnName)) && (!selfJoinColumnTableMapping.containsKey(columnName))) {
                    String columnType = tableColumnProperties.getColumnTypeName();

                    // Add Constructor parameters:-
                    paramConstructor.param(getVariableType(columnType), columnName);
                }
            }

            /* Parameterize the class variables which have been used:- */
            for (Entry<String, String> tableMapping : tableMappingAnnotations.entrySet()) {
                tableNameField = tableMapping.getKey();
                simpleClassName = "_" + tableNameField;

                if (tableMapping.getValue().contains(MAPPING_ONE_2_ONE)
                        || tableMapping.getValue().contains(MAPPING_MANY_2_ONE)) {

                    paramConstructor.param(referenceClass.get(tableNameField), simpleClassName);
                } else if (tableMapping.getValue().contains(MAPPING_ONE_2_MANY)) {

                    paramConstructor.param(
                            jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableNameField)),
                            simpleClassName);
                }

            }

            /* Used to add body to the constructor:- */
            for (Entry<String, TableColumnProperties> _columnProperties : _tableColumnPropertiesMap.entrySet()) {
                TableColumnProperties tableColumnProperties;
                String columnName = _columnProperties.getKey();
                tableColumnProperties = _columnProperties.getValue();

                if (!(tablePrimaryKeys.contains(columnName)) && (!selfJoinColumnTableMapping.containsKey(columnName))) {
                    paramConstructor.body().assign(JExpr._this().ref(columnName), JExpr.ref(columnName));
                }
            }

            /* Add the class variables in the constructor body:- */
            for (Entry<String, String> tableMapping : tableMappingAnnotations.entrySet()) {
                tableNameField = tableMapping.getKey();
                simpleClassName = "_" + tableNameField;
                paramConstructor.body().assign(JExpr._this().ref(simpleClassName), JExpr.ref(simpleClassName));
            }

        } catch (Exception e) {

            System.out.println("Exception occured while creating parametrized constructor:-");
            e.printStackTrace();
        }

    }

    /**
     * Create getter and setter methods for the respective classes:
     *
     * @param tableColProperties
     * @param tableName
     */

    public void createGetterSetterMethodsOfFieldVariables(HashMap<String, String> tableMappingAnnotations,
                                                          TableProperties tableProperties, MetaDataTablesMap DatabaseTablesMap, String tableName) {
        JMethod method = null;
        String tableNameField = "";
        String simpleClassName = "";
        Map<String, TableColumnProperties> _tableColProperties;
        List<String> tablePrimaryKeys;
        Map<String, TableProperties> _tableProperties;
        try {
            _tableProperties = DatabaseTablesMap.getDbTablePropertiesMap();
            _tableColProperties = tableProperties.getColumnProperties();
            tablePrimaryKeys = tableProperties.getPrimaryKeys();

            /* Add setter and getter methods for the variables:- */
            for (Entry<String, TableColumnProperties> _colProperties : _tableColProperties.entrySet()) {
                String columnName = _colProperties.getKey();
                TableColumnProperties _columnProperty = _colProperties.getValue();
                String colType = _columnProperty.getColumnTypeName();
                boolean colNullStatus = _columnProperty.getColumnNullStatus();
                int colSize = _columnProperty.getColumnSize();

                // Used to create the getter method:-

                if (!selfJoinColumnTableMapping.containsKey(columnName)) {
                    if (tablePrimaryKeys.size() > 1) {
                        if (!tablePrimaryKeys.contains(columnName)) {

                            if (!currentTableManyToOneColMap.contains(columnName)) {
                                method = definedClass.method(JMod.PUBLIC, getVariableType(colType), "get" + columnName);
                                method.body()._return(JExpr._this().ref(columnName));

                                method.annotate(javax.persistence.Column.class).param("name", columnName)
                                        .param("nullable", colNullStatus).param("length ", colSize);
                            }
                        }
                    } else {
                        if (!currentTableManyToOneColMap.contains(columnName)) {
                            method = definedClass.method(JMod.PUBLIC, getVariableType(colType), "get" + columnName);
                            method.body()._return(JExpr._this().ref(columnName));

                            method.annotate(javax.persistence.Column.class).param("name", columnName)
                                    .param("nullable", colNullStatus).param("length ", colSize);
                        }
                    }

                } else {

                    // Add getter Method

                    method = definedClass.method(JMod.PUBLIC, referenceClass.get(tableName), "getsup" + columnName);
                    method.body()._return(JExpr._this().ref("sup" + columnName));

                    /* Add annotation:- */
                    method.annotate(javax.persistence.ManyToOne.class).param("fetch", FetchType.LAZY).param("cascade",
                            CascadeType.ALL);

                    method.annotate(javax.persistence.JoinColumn.class).param("name	", columnName);

                    /* Used to set the getter method:- */
                    method = definedClass.method(JMod.PUBLIC,
                            jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableName)),
                            "getsub" + columnName);
                    method.body()._return(JExpr._this().ref("sub" + columnName));

                    method.annotate(javax.persistence.OneToMany.class).param("mappedBy", "sup" + columnName)
                            .param("fetch", FetchType.LAZY).param("cascade", CascadeType.ALL);

                }

                /*
                 * Add id generated Annotation:-
                 */
                if (tablePrimaryKeys.contains(columnName) && tablePrimaryKeys.size() == 1) {
                    method.annotate(javax.persistence.Id.class);
                    method.annotate(javax.persistence.GeneratedValue.class);
                }

                /* Used to create the setter method:- */
                if (!selfJoinColumnTableMapping.containsKey(columnName)) {
                    if (tablePrimaryKeys.size() > 1) {
                        if (!tablePrimaryKeys.contains(columnName)) {

                            if (!currentTableManyToOneColMap.contains(columnName)) {

                                method = definedClass.method(JMod.PUBLIC, void.class, "set" + columnName);
                                method.param(getVariableType(colType), columnName);
                                method.body().assign(JExpr._this().ref(columnName), JExpr.ref(columnName));

                            }
                        }

                    } else {
                        if (!currentTableManyToOneColMap.contains(columnName)) {
                            method = definedClass.method(JMod.PUBLIC, void.class, "set" + columnName);
                            method.param(getVariableType(colType), columnName);
                            method.body().assign(JExpr._this().ref(columnName), JExpr.ref(columnName));
                        }

                    }

                } else {

                    method = definedClass.method(JMod.PUBLIC, void.class, "setsup" + columnName);
                    method.param(referenceClass.get(tableName), "sup" + columnName);
                    method.body().assign(JExpr._this().ref("sup" + columnName), JExpr.ref("sup" + columnName));

                    method = definedClass.method(JMod.PUBLIC, void.class, "setsub" + columnName);
                    method.param(jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableName)),
                            "sub" + columnName);

                    method.body().assign(JExpr._this().ref("sub" + columnName), JExpr.ref("sub" + columnName));

                }

            }

            // Create Getter and Setter in case of Composite key
            if (tableProperties.getPrimaryKeys().size() > 1) {

                // Create Getter method of the Composite Id class:-
                method = definedClass.method(JMod.PUBLIC, embeddedClass.get(tableName + "Id"),
                        "get" + tableName + "Id");
                method.body()._return(JExpr._this().ref("_" + tableName + "Id"));

                method.annotate(javax.persistence.EmbeddedId.class);

                // Create Setter method:-
                method = definedClass.method(JMod.PUBLIC, void.class, "set" + tableName + "Id");
                method.param(embeddedClass.get(tableName + "Id"), "_" + tableName + "Id");
                method.body().assign(JExpr._this().ref("_" + tableName + "Id"), JExpr.ref("_" + tableName + "Id"));

            }

            /* Add setter and getter methods for the Class references:- */
            for (Entry<String, String> tableMapping : tableMappingAnnotations.entrySet()) {
                tableNameField = tableMapping.getKey();
                simpleClassName = "_" + tableNameField;

                if (tableMapping.getValue().contains(MAPPING_ONE_2_ONE)) {

                    /* Used to set the getter method:- */
                    method = definedClass.method(JMod.PUBLIC, referenceClass.get(tableNameField),
                            "get" + tableNameField);
                    method.body()._return(JExpr._this().ref(simpleClassName));

                    /* Add annotation:- */
                    method.annotate(javax.persistence.OneToOne.class).param("fetch", FetchType.LAZY).param("cascade",
                            CascadeType.ALL);

                    /* Used to create the setter method:- */
                    method = definedClass.method(JMod.PUBLIC, void.class, "set" + tableNameField);
                    method.param(referenceClass.get(tableNameField), simpleClassName);
                    method.body().assign(JExpr._this().ref(simpleClassName), JExpr.ref(simpleClassName));

                } else if (tableMapping.getValue().contains(MAPPING_ONE_2_MANY)) {

                    /* Used to set the getter method:- */
                    method = definedClass.method(JMod.PUBLIC,
                            jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableNameField)),
                            "get" + tableNameField);
                    method.body()._return(JExpr._this().ref(simpleClassName));

                    /* Add annotation:- */
                    method.annotate(javax.persistence.OneToMany.class).param("mappedBy", tableName)
                            .param("fetch", FetchType.LAZY).param("cascade", CascadeType.ALL);

                    /* Used to create the setter method:- */
                    method = definedClass.method(JMod.PUBLIC, void.class, "set" + tableNameField);
                    method.param(jCodeModel.ref(java.util.Set.class).narrow(referenceClass.get(tableNameField)),
                            simpleClassName);
                    method.body().assign(JExpr._this().ref(simpleClassName), JExpr.ref(simpleClassName));

                } else if (tableMapping.getValue().contains(MAPPING_MANY_2_ONE)) {

                    /* Used to set the getter method:- */
                    method = definedClass.method(JMod.PUBLIC, referenceClass.get(tableNameField),
                            "get" + tableNameField);
                    method.body()._return(JExpr._this().ref(simpleClassName));

                    /* Add annotation:- */
                    method.annotate(javax.persistence.ManyToOne.class).param("fetch", FetchType.LAZY).param("cascade",
                            CascadeType.ALL);

                    /* Fetch the primary key of the imported table:- */
                    TableProperties _tablePropertiesMap = _tableProperties.get(tableNameField);
                    String currentColumnName;
                    if (_tablePropertiesMap.getPrimaryKeys().size() == 1) {
                        TableProperties _tablePropertiesMapCurrentTable = _tableProperties.get(tableName);

                        Map<HashMap<String, String>, String> importedColumnTableMap = _tablePropertiesMapCurrentTable
                                .getImportedColumnTableAndCurrentTableColumnMapping();

                        for (Entry<HashMap<String, String>, String> imprtColMap : importedColumnTableMap.entrySet()) {
                            currentColumnName = imprtColMap.getValue();
                            HashMap<String, String> impTablesColMap = imprtColMap.getKey();
                            if (impTablesColMap.containsValue(tableNameField)) {
                                method.annotate(javax.persistence.JoinColumn.class).param("name", currentColumnName)
                                        .param("insertable", false).param("updatable", false);
                            }
                        }

                    }

                    /* Used to create the setter method:- */
                    method = definedClass.method(JMod.PUBLIC, void.class, "set" + tableNameField);
                    method.param(referenceClass.get(tableNameField), simpleClassName);
                    method.body().assign(JExpr._this().ref(simpleClassName), JExpr.ref(simpleClassName));
                }

            }

        } catch (Exception e) {

            System.out.println("Exception occured while creating the setter methods: ");
            e.printStackTrace();
        }
    }

    /**
     * Method to create private field variables for the Embedded Class:-
     */
    private void createPrivateFieldVariablesForEmbeddedClass(JDefinedClass jClass, List<String> primaryKeys,
                                                             TableProperties tableProperties) {

        String coLPropertyType = "";
        // Create Table Column properties:-
        Map<String, TableColumnProperties> columnPropertiesMap = tableProperties.getColumnProperties();
        for (String pKey : primaryKeys) {

            coLPropertyType = columnPropertiesMap.get(pKey).getColumnTypeName();
            jClass.field(JMod.PRIVATE, getVariableType(coLPropertyType), pKey).assign(JExpr.lit(2));
        }

    }

    /**
     * Add the Parameterized constructor for the embedded class:-
     */

    private void createParametrizedConstructorEmbeddedClass(JDefinedClass jClass, List<String> pKeys,
                                                            TableProperties tableProperties) {

        JMethod paramConstructor = jClass.constructor(JMod.PUBLIC);
        String coLPropertyType = "";

        /* Used to parameterize the constructor:- */
        Map<String, TableColumnProperties> columnPropertiesMap = tableProperties.getColumnProperties();

        for (String pKey : pKeys) {

            coLPropertyType = columnPropertiesMap.get(pKey).getColumnTypeName();

            /* Used to create the setter method:- */

            paramConstructor.param(getVariableType(coLPropertyType), pKey);

        }

        /* Used to add body to the constructor:- */
        for (String pKey : pKeys) {

            coLPropertyType = columnPropertiesMap.get(pKey).getColumnTypeName();

            /* Used to create the setter method:- */

            paramConstructor.body().assign(JExpr._this().ref(pKey), JExpr.ref(pKey));
        }

    }

    private void createGetterAndSetterForEmbeddedClass(JDefinedClass jClass, List<String> primaryKeys,
                                                       TableProperties tableProerties) {
        JMethod method = null;
        Map<String, TableColumnProperties> tableColumnPropertiesMap = tableProerties.getColumnProperties();
        String colPropertyType = "";
        int colSize;
        boolean colNullStatus;
        for (String pKey : primaryKeys) {
            colPropertyType = tableColumnPropertiesMap.get(pKey).getColumnTypeName();
            colSize = tableColumnPropertiesMap.get(pKey).getColumnSize();
            colNullStatus = tableColumnPropertiesMap.get(pKey).getColumnNullStatus();
            // Create getter methods:-
            method = jClass.method(JMod.PUBLIC, getVariableType(colPropertyType), "get" + pKey);
            method.body()._return(JExpr._this().ref(pKey));

            method.annotate(javax.persistence.Column.class).param("name", pKey).param("nullable", colNullStatus)
                    .param("length ", colSize);

            // Create setter methods:-
            method = jClass.method(JMod.PUBLIC, void.class, "set" + pKey);
            method.param(getVariableType(colPropertyType), pKey);
            method.body().assign(JExpr._this().ref(pKey), JExpr.ref(pKey));
        }

    }

    /**
     * Create Embedded class for Tables:-
     *
     * @param tableName
     * @param tableColProperties
     * @param primaryKeys
     * @param tableProperties
     */

    /* Create embedded class for the table which has composite key:- */
    public void createEmbeddedClass(String tableName, TableProperties tableProperties) {

        JDefinedClass embedClass;
        JMethod methodBody;
        JConditional jcondition;
        JBlock jblock;
        String className = "";
        JExpression expr = null;
        try {

            className = tableName + "Id";

            JCodeModel jCode = new JCodeModel();

            JPackage jCodePackage = jCode._package("com.xassure.dbTables");

            embedClass = jCodePackage._class(className)._implements(java.io.Serializable.class);

            embeddedClass.put(tableName + "Id", embedClass);

            jCode.ref("java.util.*");

            // Add Class Heading Annotation:-
            addClassHeadingAnnotationEmbeddedClass(tableName);

            List<String> primaryKeysEmbeddedClass = tableProperties.getPrimaryKeys();

            /* create private field variables:- */
            createPrivateFieldVariablesForEmbeddedClass(embedClass, primaryKeysEmbeddedClass, tableProperties);

            /* create default constructor:- */
            createDefaultConstructor(embedClass);

            /* Create parameterized constructor */
            createParametrizedConstructorEmbeddedClass(embedClass, primaryKeysEmbeddedClass, tableProperties);

            /* create the getter and setter methods */
            createGetterAndSetterForEmbeddedClass(embedClass, primaryKeysEmbeddedClass, tableProperties);

            /* Create equals Method:- */
            methodBody = embedClass.method(JMod.PUBLIC, boolean.class, "equals");
            JVar jvar = methodBody.param(Object.class, "obj");
            jcondition = methodBody.body()._if(jvar.eq(JExpr._null()));
            jcondition._then()._return(JExpr.FALSE);

            jblock = methodBody.body();
            // JDefinedClass dynamicClass = jCode._class(className);
            JVar var2 = jblock.decl(embedClass, "obj2", JExpr.cast(jCode.parseType(className), JExpr.ref("obj")));

            jcondition = jblock
                    ._if(JExpr._this().invoke("getClass").invoke("equals").arg(jvar.invoke("getClass")).not());
            jcondition._then()._return(JExpr.FALSE);
            int count = 0;
            String colPropertyType = "";

            JExpression tempExpr = null;

            Map<String, TableColumnProperties> colProperties = tableProperties.getColumnProperties();

            for (String pKey : primaryKeysEmbeddedClass) {
                colPropertyType = colProperties.get(pKey).getColumnTypeName();

                if (colPropertyType.toLowerCase().contains("int")) {

                    expr = JExpr._this().ref(pKey).eq(JExpr.ref("obj2").invoke("get" + pKey));
                } else if (colPropertyType.toLowerCase().contains("varchar")) {

                    expr = JExpr._this().ref(pKey).invoke("equals").arg(var2.invoke("get" + pKey));
                } else if (colPropertyType.toLowerCase().contains("number")) {

                    expr = JExpr._this().ref(pKey).eq(JExpr.ref("obj2").invoke("get" + pKey));
                }

                if (count != 0) {
                    expr = tempExpr.cand(expr);

                }
                tempExpr = expr;
                count++;
            }

            jcondition = jblock._if(expr);
            jcondition._then()._return(JExpr.TRUE);

            jblock._return(JExpr.FALSE);

            /* Create hashcode method:- */
            methodBody = embedClass.method(JMod.PUBLIC, int.class, "hashcode");

            jblock = methodBody.body();

            JVar varHashcode = jblock.decl(jCode.INT, "tmp", JExpr.lit(0));
            String reference = null;

            int countPrimaryKeys = 0;
            for (String pKey : primaryKeysEmbeddedClass) {

                if (countPrimaryKeys == 0) {
                    reference = pKey;
                } else {

                    reference = reference + "+" + pKey;
                }

                countPrimaryKeys++;
            }

            jblock.assign(varHashcode, JExpr.ref("(" + reference + ")").invoke("hashCode"));

            jblock._return(varHashcode);

            /* Building class at given location:- */
            // jCode.build(new File("HibernateFramework"));
            jCode.build(new File(DB_POJOLOCATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create dynamic pojos bases upon the Meta Data:-
     *
     * @throws IOException
     */
    private void createPojoClasses(String dbName, MetaDataTablesMap DatabaseTablesMap, String pojoLocation)
            throws JClassAlreadyExistsException, IOException {

        List<String> dbTableNames;
        Map<String, TableProperties> DatabaseTablePropertiesMapping;
        TableProperties tableProperties;
        HashMap<String, String> dbTableMapping;

        jCodeModel = new JCodeModel();

        // Class Level imports:-
        jCodeModel.ref("java.util.*");

        JPackage jCodePackage = jCodeModel._package("com.xassure.dbTables");

        // Fetch all the Db Tables:-
        dbTableNames = DatabaseTablesMap.getDbTableNames();

        // Fetch the DB Tables and Properties mapping:-
        DatabaseTablePropertiesMapping = DatabaseTablesMap.getDbTablePropertiesMap();

        // Create Reference Classes:-
        for (String tableName : dbTableNames) {
            JDefinedClass runtimeClass = jCodePackage._class(tableName);
            referenceClass.put(tableName, runtimeClass);
        }

        // Iterate Over tables:-
        for (String tableName : dbTableNames) {
            definedClass = referenceClass.get(tableName);
            // definedClass = jCodePackage._class(tableName);

            tableProperties = DatabaseTablePropertiesMapping.get(tableName);

            // Create Embedded Reference classes:-
            if (tableProperties.getPrimaryKeys().size() > 1) {
                // JDefinedClass runtimeClass = jCodePackage._class(tableName +
                // "Id");
                // referenceClass.put(tableName + "Id", runtimeClass);
                createEmbeddedClass(tableName, tableProperties);
            }

            // Fetch Table primary Keys:-
            tableProperties.getPrimaryKeys();

            dbTableMapping = creatDBTableMapping(DatabaseTablesMap, tableProperties, tableName);

            // Add Class Heading Annotation:-
            addClassHeadingAnnotation(tableName);

            // Add Class private variables:-
            createPrivateFieldVariables(tableProperties, dbTableMapping, tableName);

            // Create the default Constructor of the class:-
            createDefaultConstructor(definedClass);

            // Create parameterized constructor:
            // createParametrizedConstructor(tableProperties, dbTableMapping);

            // Create Setter methods:-
            createGetterSetterMethodsOfFieldVariables(dbTableMapping, tableProperties, DatabaseTablesMap, tableName);

            /* Building class at given location:- */
            // jCodeModel.build(new File("HibernateFramework"));
            jCodeModel.build(new File(pojoLocation));
            currentTableManyToOneColMap.clear();

        }

        // Add the Tables for the embedded class :-
        for (Entry<String, JDefinedClass> embeddedClasses : embeddedClass.entrySet()) {
            dbTableNames.add(embeddedClasses.getKey());
        }
    }

    /**
     * Method used to read the database and create Pojo's:-
     */
    public void readDBAndCreatePojo(String DBName, String url, String userName, String password, String schemaName,
                                    String pojoLocation, List<String> tableList) {
        try {

            MetaDataTablesMap _dbTablesMap = new ReadDatabases().readDatabase(DBName, url, userName, password,
                    schemaName, tableList);

            createPojoClasses(DBName, _dbTablesMap, pojoLocation);

            List<String> tableNames = _dbTablesMap.getDbTableNames();

            new CustomXmlGenerator().customXMLCreation(DBName, url, userName, password, tableNames, pojoLocation);

        } catch (Exception e) {

            System.out.println("Exception occured while reading and creating pojo's");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        System.out.println("testing");
        List<String> tableList = new ArrayList<String>();
        tableList.add("tb_catent_desc");

        new CreatePojo("classicmodels", "jdbc:mysql://localhost:3306/classicmodels", "root", "rootroot",
                "classicmodels", "src/main/java").readDBAndCreatePojo("db_mysql",
                "jdbc:mysql://localhost:3306/classicmodels", "root", "rootroot", "classicmodels",
                "src/main/java", null);
    }
}
