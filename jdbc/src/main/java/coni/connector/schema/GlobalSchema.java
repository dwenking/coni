package coni.connector.schema;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provide schema info and utils
 */
public class GlobalSchema {
    private Connection con;
    private String db;
    private List<Table> dbTables;
    private Random r;

    public void renewTables() throws SQLException {
        this.dbTables.clear();
        DatabaseMetaData metaData = con.getMetaData();
        ResultSet tables = metaData.getTables(db, null, null, null);

        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            ResultSet columns = metaData.getColumns(db, null, tableName, null);
            List<Column> tmpCols = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                tmpCols.add(new Column(columnName, columnType));
            }
            dbTables.add(new Table(tableName, tmpCols));
        }
    }

    public Table getRandomTable() {
        if (dbTables == null || dbTables.isEmpty()) {
            throw new IllegalArgumentException("No table to select!");
        }

        int randomIndex = r.nextInt(dbTables.size());
        return dbTables.get(randomIndex);
    }

    public String genTablePreparedInsert(Table table) {
        String name = table.getName();
        int col = table.getCols().size();
        StringBuffer tmp = new StringBuffer("INSERT INTO ");
        tmp.append(name);
        tmp.append(" VALUES(?");
        for (int i = 0; i < col - 1; i++) {
            tmp.append(", ?");
        }
        tmp.append(");");

        return tmp.toString();
    }

    public GlobalSchema(Connection con, String db, Random r) {
        this.con = con;
        this.db = db;
        this.r = r;
        this.dbTables = new ArrayList<>();
    }
}
