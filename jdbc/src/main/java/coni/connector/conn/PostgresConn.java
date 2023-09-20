package coni.connector.conn;

import coni.connector.schema.MySQLGlobalSchema;
import coni.connector.schema.PostgresGlobalSchema;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import static coni.GlobalConfiguration.*;
import static coni.GlobalConfiguration.password;

public class PostgresConn extends Conn{
    public PostgresConn(Random r, String jdbc, String db, String config) throws SQLException {
        super(r, jdbc, db, config);
        initConnAndSchema(jdbc, db, config);
    }

    private void initConnAndSchema(String jdbc, String db, String config) throws SQLException {
        String url;
        if (config.startsWith("&")) {
            url = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s%s", jdbc, dbHost, dbPort, db, username, password, config);
        } else {
            url = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s&%s", jdbc, dbHost, dbPort, db, username, password, config);
        }
        logger.error("{} Connecting to {}", owner, url);
        this.conn = DriverManager.getConnection(url);
        this.schema = new PostgresGlobalSchema(this.conn, db, r);
    }
}
