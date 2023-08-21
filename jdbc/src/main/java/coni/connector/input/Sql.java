package coni.connector.input;

/**
 * wrapper
 */
public class Sql {
    public String sql;

    public Sql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}