package com.xassure.dbControls;

import com.google.inject.Inject;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class DatabaseControlLibrary implements DatabaseControls {

    Session session;

    @Inject
    public DatabaseControlLibrary(Session session) {
        this.session = session;
    }

    @Override
    public List readAllDataFromTable(String tableName) {
        // TODO Auto-generated method stub

        // Session session =
        // GetSessionFactory.getSessionFactory().openSession();
        Transaction transaction = null;
        List tableData = null;
        try {
            transaction = session.beginTransaction();
            String hql = "from " + tableName;
            tableData = session.createQuery(hql).list();
            transaction.commit();
            if (tableData.size() == 0) {
                Reporting.getLogger().log(LogStatus.INFO, "No Data exist in the table");
            } else {
                // Reporting.getLogger().log(LogStatus.PASS, "Successfuly read
                // data from the table: " + tableName);
            }

        } catch (HibernateException e) {

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while reading all the data from the Table " + tableName);

            e.printStackTrace();
        }
        return tableData;

    }

    @Override
    public List readAllDataFromTableUsingCriterion(String tableName) {
        // Session session =
        // GetSessionFactory.getSessionFactory().openSession();
        Transaction transaction = null;
        List tableData = null;
        try {
            transaction = session.beginTransaction();
            Class ref = Class.forName("com.xassure.dbTables." + tableName);
            Criteria crit = session.createCriteria(ref);
            tableData = crit.list();
            transaction.commit();
            if (tableData.size() == 0) {
                Reporting.getLogger().log(LogStatus.INFO, "No Data exist in the table");
            } else {
                Reporting.getLogger().log(LogStatus.PASS, "Successfuly read data from the table: " + tableName);
            }

        } catch (HibernateException e) {
            // Reporting.setDescriptionAndStatus(
            // "Exception occured while reading all the data from the Table " +
            // tableName + " based upon the primary keys", false);

            // LoggerUtil.logError(e, "put", currentClassName, thisMethodName,
            // currentDate, currentTime);
            e.printStackTrace();

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while reading all the data from the Table " + tableName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return tableData;

    }

    @Override
    public List readAllDataFromTableByConditions(String tableName, Map<String, Object> columnNameAndValueMap) {
        // TODO Auto-generated method stub

        // Session session =
        // GetSessionFactory.getSessionFactory().openSession();
        Transaction transaction = null;
        List tableData = null;
        try {
            transaction = session.beginTransaction();
            // Create the class ref
            Class classRef = Class.forName("com.xassure.dbTables." + tableName.toLowerCase());

            // Map<String, Object> columnNameAndValueMap = new HashMap<String,
            // Object>();

            Criteria crit = session.createCriteria(classRef);

            Map<String, HashMap<String, Object>> colNameAndValueMap = getColumnType(columnNameAndValueMap, classRef);

            for (Entry<String, HashMap<String, Object>> colNameAndValue : colNameAndValueMap.entrySet()) {
                String colName = colNameAndValue.getKey();
                HashMap<String, Object> colTypeAndValueMap = colNameAndValue.getValue();

                for (Entry<String, Object> colTypeAndValue : colTypeAndValueMap.entrySet()) {

                    if (colTypeAndValue.getKey().contains("string")) {
                        String colVal = (String) colTypeAndValue.getValue();
                        crit.add(Restrictions.eq(colName, colVal));
                    } else if (colTypeAndValue.getKey().contains("int")) {
                        int colVal = (Integer) colTypeAndValue.getValue();
                        crit.add(Restrictions.eq(colName, colVal));
                    } else if (colTypeAndValue.getKey().contains("double")) {
                        double colVal = (Double) colTypeAndValue.getValue();
                        crit.add(Restrictions.eq(colName, colVal));
                    } else if (colTypeAndValue.getKey().contains("date")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date dateVal = formatter.parse("2005-05-31");
                        crit.add(Restrictions.eq(colName, dateVal));
                    }
                }

            }

            tableData = crit.list();
            transaction.commit();
            session.close();

            Reporting.getLogger().log(LogStatus.PASS, "Read Query has been executed sucessfully");

        } catch (HibernateException e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while reading all the data from the Table "
                    + tableName + "based upon the conditions");

            // LoggerUtil.logError(e, "put", currentClassName, thisMethodName,
            // currentDate, currentTime);
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return tableData;

    }

    @Override
    public List readDataFromTableByPrimaryKey(String tableName, String primaryColumnName, String primaryKeyValue) {
        // TODO Auto-generated method stub

        // Session session =
        // GetSessionFactory.getSessionFactory().openSession();
        Transaction transaction = null;
        List tableData = null;
        Map<String, HashMap<String, Object>> tablePrimaryKeyMapping = null;
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        String primaryKeyColumnName = "";

        try {

//			 tablePrimaryKeyMapping = readData.getPrimaryKeys(tableName);
            primaryKeys = tablePrimaryKeyMapping.get(tableName);
            if (primaryKeys.size() == 1) {
                for (String key : primaryKeys.keySet()) {

                    primaryKeyColumnName = (String) primaryKeys.get(key);
                }
            }

            transaction = session.beginTransaction();
            String hql = "from " + tableName + "where " + primaryKeyColumnName + " =" + primaryKeyValue;
            tableData = session.createQuery(hql).list();
            if (tableData.size() != 0) {
                Reporting.getLogger().log(LogStatus.PASS, "Data found for primary key: " + primaryKeyValue);
            }

        } catch (HibernateException e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while reading all the data from the Table "
                    + tableName + " based upon the primary keys");

            // LoggerUtil.logError(e, "put", currentClassName, thisMethodName,
            // currentDate, currentTime);
            e.printStackTrace();

        }

        return tableData;

    }

    @Override
    public void updateDB(String tableName, Map<String, Object> updateColValues, Map<String, Object> updateCondition) {
        // TODO Auto-generated method stub

        // Session session =
        // GetSessionFactory.getSessionFactory().openSession();
        Transaction transaction = null;
        String hql_Query = "UPDATE " + tableName + " set ";
        try {
            Class classRef = Class.forName("com.xassure.dbTables." + tableName.toLowerCase());
            transaction = session.beginTransaction();

            int updateColSize = updateColValues.size();
            int count = 0;
            getColumnType(updateColValues, classRef);
            // Add the Column Values to statement which needs to be updated:-
            for (Entry<String, Object> updateColVal : updateColValues.entrySet()) {

                String colName = updateColVal.getKey();
                hql_Query = hql_Query + colName + " = :" + colName;
                count++;
                if (count < updateColSize) {

                    hql_Query = hql_Query + " , ";
                }
            }

            // Add the Where clause:-

            hql_Query = hql_Query + " WHERE id = :";

            for (Entry<String, Object> updateColVal : updateCondition.entrySet()) {

                String colName = updateColVal.getKey();
                hql_Query = hql_Query + colName;

            }

            Query query = session.createQuery(hql_Query);

            // Set the Update Column Values:-
            for (Entry<String, Object> updateColVal : updateColValues.entrySet()) {

                String colName = updateColVal.getKey();
                query.setParameter(colName, updateColVal.getValue());
            }

            // Set the Where condition column Values:-
            for (Entry<String, Object> updateColVal : updateCondition.entrySet()) {

                String colName = updateColVal.getKey();
                query.setParameter(colName, updateColVal.getValue());
            }

            query.executeUpdate();

            transaction.commit();

            Reporting.getLogger().log(LogStatus.PASS, "Update Query has been executed sucessfully");

        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Update Query Exception");
            e.printStackTrace();
        } finally {
            session.close();

        }

    }

    /*
     * Get return type of the column Name:_
     */
    private Map<String, HashMap<String, Object>> getColumnType(Map<String, Object> columnNameAndValueMap,
                                                               Class<?> classRef) {

        Map<String, HashMap<String, Object>> colNameAndTypeMap = new HashMap<String, HashMap<String, Object>>();
        String returnType = null;
        String colName = null;
        boolean check = false;

        Method[] classMethods = classRef.getMethods();

        for (Entry<String, Object> colValMap : columnNameAndValueMap.entrySet()) {
            // Fetch the Column name:-
            String columnName = colValMap.getKey().toLowerCase();
            HashMap<String, Object> colTypeAndValueMap = new HashMap<String, Object>();
            // Fetch the Class Methods:-
            for (Method methd : classMethods) {

                if (methd.getName().toLowerCase().contains("get")) {
                    System.out.println(methd.getName() + " Method Name");
                    if (methd.getName().toLowerCase().contains(columnName)) {
                        colName = methd.getName().split("get")[1];
                        String m_returnType = methd.getReturnType().toString();
                        if (m_returnType.toLowerCase().contains("string")) {
                            returnType = "string";
                            check = true;
                            break;
                        } else if (m_returnType.toLowerCase().contains("int")) {

                            returnType = "int";
                            check = true;
                            break;

                        } else if (m_returnType.toLowerCase().contains("double")) {

                            returnType = "double";
                            check = true;
                            break;

                        } else if (m_returnType.toLowerCase().contains("date")) {

                            returnType = "date";
                            check = true;
                            break;

                        }

                    }

                }

            }

            if (check) {
                colTypeAndValueMap.put(returnType, colValMap.getValue());
                colNameAndTypeMap.put(colName, colTypeAndValueMap);
            }
            check = false;
        }

        return colNameAndTypeMap;
    }

    @Override
    public void insertIntoDB(Object classObject) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(classObject);
            transaction.commit();

        } catch (HibernateException e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while inserting data");

            e.printStackTrace();
        }

    }

}
