package com.xassure.dbControls.manageDatabase;

import java.util.List;
import java.util.Map;

public class MetaDataTablesMap {

    private List<String> dbTableNames;
    private Map<String, TableProperties> dbTablePropertiesMap;

    /*
     * Get the DB Table names:-
     */
    public List<String> getDbTableNames() {
        return dbTableNames;
    }

    /*
     * Set the DB TableNames:-
     */
    public void setDbTableNames(List<String> dbTableNames) {
        this.dbTableNames = dbTableNames;
    }

    /*
     * get DB Table Properties:-
     */
    public Map<String, TableProperties> getDbTablePropertiesMap() {
        return dbTablePropertiesMap;
    }

    /*
     * Set the DBTable Properties:-
     */
    public void setDbTablePropertiesMap(Map<String, TableProperties> dbTablePropertiesMap) {
        this.dbTablePropertiesMap = dbTablePropertiesMap;
    }

}
