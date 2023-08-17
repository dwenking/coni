package coni.connector.conn;

import java.io.BufferedWriter;
import java.sql.SQLException;
import java.util.Random;

/**
 * Log detailed runtime info
 */
public class ConnLogger extends Conn {
    private static final String logPath = "logs/analysis/";
    private BufferedWriter writer;

    public ConnLogger(Random r, String jdbc, String db, String config) throws SQLException {
        super(r, jdbc, db, config);
    }
}
