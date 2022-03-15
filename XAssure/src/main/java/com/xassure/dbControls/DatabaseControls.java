package com.xassure.dbControls;

import java.util.List;
import java.util.Map;

public interface DatabaseControls {

    List readAllDataFromTable(String tableName);

    List readAllDataFromTableUsingCriterion(String tableName);

    List readAllDataFromTableByConditions(String tableName, Map<String, Object> columnNameAndValueMap);

    List readDataFromTableByPrimaryKey(String tableName, String primaryColumnName, String primaryKeyValue);

    void updateDB(String tableName, Map<String, Object> updateColValues, Map<String, Object> updateCondition);

    void insertIntoDB(Object classObject);

}
