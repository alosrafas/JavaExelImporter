public class ValuesToAdd {
    private Integer columId;
    private String columnValue;
    private Integer columNumber;

    public ValuesToAdd(Integer columNumber, Integer columId, String columnValue) {
        this.columNumber = columNumber;
        this.columnValue = columnValue;
        this.columId = columId;
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
}
