package models;

public class ValuesToAdd {
    private Integer columId;
    private String columnValue;
    private Integer columNumber;
    private String columnName;

    public ValuesToAdd(Integer columNumber, Integer columId, String columnValue) {
        this.columNumber = columNumber;
        this.columnValue = columnValue;
        this.columId = columId;
    }

    public ValuesToAdd(Integer columNumber, Integer columId, String columnValue, String columnName) {
        this.columNumber = columNumber;
        this.columnValue = columnValue;
        this.columId = columId;
        this.columnName = columnName;
    }

    public Integer getColumId() {
        return columId;
    }

    public void setColumId(Integer columId) {
        this.columId = columId;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public Integer getColumNumber() {
        return columNumber;
    }

    public void setColumNumber(Integer columNumber) {
        this.columNumber = columNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
