package coni.connector.schema;

public class Column {
    private String name;
    private String type;
    private Table table;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
