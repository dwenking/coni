package coni.connector.conn;

import java.sql.SQLException;
import java.util.Random;

public class PostgresConn extends Conn{
    public PostgresConn(Random r, String jdbc, String db, String config) throws SQLException {
        super(r, jdbc, db, config);
    }
}
