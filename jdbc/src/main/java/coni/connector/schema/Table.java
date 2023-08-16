package coni.connector.schema;

import java.util.List;

public class Table {
    private String name;
    private List<Column> cols;

    public String getName() {
        return name;
    }

    public List<Column> getCols() {
        return cols;
    }

    public Table(String name, List<Column> cols) {
        this.name = name;
        this.cols = cols;
        for (Column col : cols) {
            col.setTable(this);
        }
    }
}
