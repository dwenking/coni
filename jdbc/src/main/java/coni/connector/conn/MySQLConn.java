package coni.connector.conn;

import coni.connector.schema.GlobalSchema;
import coni.connector.schema.MySQLGlobalSchema;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static coni.GlobalConfiguration.*;
import static coni.GlobalConfiguration.password;

public class MySQLConn extends Conn{
    public MySQLConn(Random r, String jdbc, String db, String config) throws SQLException {
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
        this.schema = new MySQLGlobalSchema(this.conn, db, r);
    }

    @Override
    protected String getColumnValueByType(ResultSet rs, List<String> colType, int idx) throws SQLException {
        String type = colType.get(idx - 1);
        switch (type) {
            case "DOUBLE":
                return String.valueOf(rs.getDouble(idx));
            case "FLOAT":
                return String.valueOf(rs.getFloat(idx));
            case "DECIMAL":
                return String.valueOf(rs.getBigDecimal(idx));
            case "INT":
                return String.valueOf(rs.getInt(idx));
            case "VARCHAR":
            default:
                return rs.getString(idx);
        }
    }
}
