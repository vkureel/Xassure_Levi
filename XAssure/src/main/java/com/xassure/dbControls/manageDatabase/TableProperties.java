package com.xassure.dbControls.manageDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableProperties {

    private Map<String, TableColumnProperties> tableColumnProperties;
    private List<String> primaryKeys;
    private List<String> uniqueKeys;
    Map<String, String> importedTableColumnNames;
    Map<String, String> exportedTableAndExportedTableColumnName;
    Map<String, String> currentColumnAndImportedTableName;
    HashMap<String, String> exportedTableAndCurrentColumnName;
    Map<String, String> currentTableExportedTableandExportedColumnName;
    private int totalColumnCount;
    private Map<HashMap<String, String>, String> currentTableColumnAndExportedTableColumnMapping;
    private Map<HashMap<String, String>, String> currentTableColumnAndImportedTableColumnMapping;

    /*
     * Get Table Column Properties:-
     */
    public Map<String, TableColumnProperties> getColumnProperties() {
        return tableColumnProperties;
    }

    /*
     * Set the column properties:-
     */
    public void setColumnProperties(Map<String, TableColumnProperties> tableColumnProperties) {
        this.tableColumnProperties = tableColumnProperties;
    }

    /*
     * Get the primary Keys:-
     */
    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    /*
     * Set Primary Keys:-
     */
    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    /*
     * Get table unique Keys:-
     */
    public List<String> getUniqueKeys() {
        return uniqueKeys;
    }

    /*
     * Set Unique keys:-
     */
    public void setUniqueKeys(List<String> uniqueKeys) {
        this.uniqueKeys = uniqueKeys;
    }

    /*
     * get Imported Table and Imported Column Name:-
     *
     * public Map<String, String> getImportedTableColumnNames() { return importedTableColumnNames; }
     */

    /*
     *
     * Set the Imported table And Column Name:-
     *
     * public void setImportedTableColumnNames(Map<String, String> importedTableColumnNames) {
     * this.importedTableColumnNames = importedTableColumnNames; }
     *
     *
     * Get Current Table Column Name and Imported Table column Name:-
     *
     * public Map<String, String> getExportedTableAndExportedTableColumnName() { return
     * exportedTableAndExportedTableColumnName; }
     */
    /*
     * Set the current Column Name and Imported table Column Name:-
     *
     * public void setExportedTableAndExportedTableColumnName(Map<String, String>
     * currentColumnAndImportedColumnName) { this.exportedTableAndExportedTableColumnName =
     * currentColumnAndImportedColumnName; }
     *
     *
     * Get the Current Table Column Name and Imported table Name:-
     *
     * public Map<String, String> getCurrentTableColumnNameAndImportedTableName() { return
     * currentColumnAndImportedTableName; }
     */
    /*
     * Set the Current Table Column Name and Imported table Name:-
     *
     * public void setCurrentTableColumnNameAndImportedTableName(Map<String, String>
     * currentColumnAndImportedTableName) { this.currentColumnAndImportedTableName =
     * currentColumnAndImportedTableName; }
     *
     *
     * Get the current Column Name and Exported table Name:-
     *
     * public HashMap<String, String> getExportedTableNameAndCurrentTableColumnName() { return
     * exportedTableAndCurrentColumnName; }
     *
     *
     * Set the current Column Name and Exported table Name:-
     *
     * public void setExportedTableNameAndCurrentTableColumnName(HashMap<String, String>
     * currentColumnAndExportedTableName) { this.exportedTableAndCurrentColumnName =
     * currentColumnAndExportedTableName; }
     *
     *
     * Get the Current column name and Exported Column Name:-
     *
     * public Map<String, String> getCurrentColumnAndExportedColumnName() { return
     * currentTableExportedTableandExportedColumnName; }
     */

    /*
     * Get the Current column name and Exported Column Name:-
     *
     *
     * public void setCurrentColumnAndExportedColumnName(Map<String, String> currentColumnAndExportedColumnName) {
     * this.currentTableExportedTableandExportedColumnName = currentColumnAndExportedColumnName; }
     */
    /*
     * Get total tables count:-
     */
    public int getTotalColumnsCount() {

        return totalColumnCount;
    }

    /*
     * Set total Tables Count:-
     */
    public void setTotalColumnCount(int totalColumnsCount) {

        this.totalColumnCount = totalColumnsCount;
    }

    /*
     * get Current Table Column Name and Exported Table:Column Mapping:-
     */
    public Map<HashMap<String, String>, String> getExportedColumnTableAndCurrentTableColumnMapping() {

        return this.currentTableColumnAndExportedTableColumnMapping;
    }

    /*
     * Set the current Table column name and Exported Table:Column Mapping:-
     *
     */
    public void setExportedColumnTableAndCurrentTableColumnMapping(
            Map<HashMap<String, String>, String> currentTableColumnAndExportedTableColumnMapping) {

        this.currentTableColumnAndExportedTableColumnMapping = currentTableColumnAndExportedTableColumnMapping;
    }

    /*
     * get current table and Imported Table:Column Mapping:-
     */
    public Map<HashMap<String, String>, String> getImportedColumnTableAndCurrentTableColumnMapping() {
        return this.currentTableColumnAndImportedTableColumnMapping;
    }

    /*
     * Set current table column Name and imported table:column Mapping:-
     */
    public void setImportedColumnTableAndCurrentTableColumnMapping(
            Map<HashMap<String, String>, String> currentTableColumnAndImportedTableColumnMapping) {

        this.currentTableColumnAndImportedTableColumnMapping = currentTableColumnAndImportedTableColumnMapping;
    }

}
