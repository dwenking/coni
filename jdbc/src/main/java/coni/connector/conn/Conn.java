package coni.connector.conn;

import coni.connector.input.BatchSql;
import coni.connector.input.Sql;
import coni.connector.result.*;
import coni.connector.schema.Column;
import coni.connector.schema.GlobalSchema;
import coni.connector.schema.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static coni.GlobalConfiguration.*;

/**
 * Wrapper for many jdbc scenarios
 */
public class Conn {

    protected Random r;
    protected String owner;
    protected String db;
    protected Connection conn;
    protected GlobalSchema schema;
    protected static final Logger logger = LogManager.getLogger("Conn");

    public Result execute(Sql s) {
        String sql = s.sql;
        try (Statement stmt= this.conn.createStatement()){
            boolean hasResultSet = stmt.execute(sql);

            if (hasResultSet) {
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int count = rsMetaData.getColumnCount();

                List<String> res = new ArrayList<>();
                StringBuffer sb = new StringBuffer();
                for (int i = 1; i <= count; i++) {
                    sb.append("* " + rsMetaData.getColumnTypeName(i) + " *");
                }
                res.add(sb.toString());

                while (rs.next()) {
                    sb.setLength(0);

                    for (int i = 1; i <= count; i++) {
                        sb.append("* " + rs.getString(i) + " *");
                    }
                    res.add(sb.toString());
                }
                return new NormalResult(this.owner, Collections.singletonList(sql), res);
            } else {
                int count = stmt.getUpdateCount();
                return new UpdateResult(this.owner, Collections.singletonList(sql), count);
            }
        } catch (SQLException e) {
                return new ErrorResult(this.owner, Collections.singletonList(sql), e.toString());
        }
    }

    public Result executeBatch(BatchSql bs) {
        Sql[] sqls = bs.sqls;
        List<String> tmp = new ArrayList<>();
        try (Statement stmt= this.conn.createStatement()){
            for (Sql s : sqls) {
                stmt.addBatch(s.sql);
                tmp.add(s.sql);
            }
            int[] res = stmt.executeBatch();
            return new BatchResult(this.owner, tmp, res);
        } catch (SQLException e) {
            return new ErrorResult(this.owner, tmp, e.toString());
        }
    }

    public Result executeBatchPreparedInsert() throws SQLException {
        this.schema.renewTables();
        Table t = this.schema.getRandomTable();
        List<Column> cols = t.getCols();

        try (PreparedStatement stmt= this.conn.prepareStatement(this.schema.genTablePreparedInsert(t))){
            for (int i = 0; i < this.r.nextInt(10); i++) {
                for (int j = 1; j <= cols.size(); j++) {
                    stmt.setObject(j, r.nextInt());
                }
                stmt.addBatch();
            }
            int[] res = stmt.executeBatch();
            return new BatchResult(this.owner, Collections.singletonList("INSERT INTO t0 values(?, ?)"), res);
        } catch (SQLException e) {
            return new ErrorResult(this.owner, Collections.singletonList("INSERT INTO t0 values(?, ?)"), e.toString());
        }
    }

    private boolean checkBatch(String type) {
        if ("SET".equals(type) || "SELECT".equals(type) ||
            "CHECK".equals(type) || "CHECKSUM".equals(type) ||
            "ANALYZE".equals(type) || "OPTIMIZE".equals(type) ||
            "REPAIR".equals(type) || "ALTER".equals(type) ||
            "CREATE".equals(type) || "DROP".equals(type) || "USE".equals(type)) {
            return false;
        }
        return true;
    }

    public Conn(Random r, String jdbc, String db) throws SQLException {
        this.r = r;
        this.owner = jdbc;
        this.db = db;
        initConnAndSchema(jdbc, db);
    }

    private void initConnAndSchema(String jdbc, String db) throws SQLException {
        String config = ConnectorUtil.getRandomUrlConfig(r);
        String url = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s", jdbc, host, port, db, username, password, config);

        this.conn = DriverManager.getConnection(url);
        this.schema = new GlobalSchema(this.conn, db, r);
        logger.info("{} Connecting to {}", owner, url);
    }
}
