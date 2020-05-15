package com.xck;

public class MissionConfig {
    private String sourceTable;
    private String destTable;
    private String limit = "1000";
    private String primaryKey;
    private String timeColumn;
    private volatile boolean isRunnable;
    private String idealCheck = "3000";
    private String whereCondition = "1=1";

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getDestTable() {
        return destTable;
    }

    public void setDestTable(String destTable) {
        this.destTable = destTable;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTimeColumn() {
        return timeColumn;
    }

    public void setTimeColumn(String timeColumn) {
        this.timeColumn = timeColumn;
    }

    public boolean isRunnable() {
        return isRunnable;
    }

    public void setRunnable(boolean runnable) {
        isRunnable = runnable;
    }

    public String getIdealCheck() {
        return idealCheck;
    }

    public void setIdealCheck(String idealCheck) {
        this.idealCheck = idealCheck;
    }

    public String getWhereCondition() {
        return whereCondition;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }
}
