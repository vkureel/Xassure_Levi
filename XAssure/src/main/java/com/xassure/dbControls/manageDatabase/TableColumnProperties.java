package com.xassure.dbControls.manageDatabase;

public class TableColumnProperties {
    private int columnSize;
    private boolean columnNullStatus;
    private String columnTypeName;


    /*
     * Get Column Size of Table:-
     */
    public int getColumnSize() {
        return columnSize;
    }

    /*
     * Set Column Size of Table:-
     */
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    /*
     * Get Column Null Status value:-
     */
    public boolean getColumnNullStatus() {
        return columnNullStatus;
    }

    /*
     * Set Column Null Status value:-
     */
    public void setColumnNullStatus(boolean columnNullStatus) {
        this.columnNullStatus = columnNullStatus;
    }

    /*
     * Get Column Type name:-
     */
    public String getColumnTypeName() {
        return columnTypeName;
    }

    /*
     * Set column Type name:-
     */
    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }



    /*
     * set the column name:-
     *
     * public void setColumnName(String columnName) { this.columnName = columnName; }
     *
     *
     * get the Column Name:-
     *
     * public String getColumnName() {
     *
     * return columnName; }
     */
}
