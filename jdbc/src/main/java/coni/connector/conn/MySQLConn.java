package coni.connector.conn;

import java.sql.SQLException;
import java.util.Random;

public class MySQLConn extends Conn{
    public MySQLConn(Random r, String jdbc, String db, String config) throws SQLException {
        super(r, jdbc, db, config);
    }
}
