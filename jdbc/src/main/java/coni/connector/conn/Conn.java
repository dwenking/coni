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
import java.util.*;

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
                List<String> colType = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    colType.add(rsMetaData.getColumnTypeName(i));
                    sb.append("* " + rsMetaData.getColumnTypeName(i) + " *");
                }
                res.add(sb.toString());

                while (rs.next()) {
                    sb.setLength(0);
                    for (int i = 1; i <= count; i++) {
                        sb.append("* " + getColumnValueByType(rs, colType, i) + " *");
                    }
                    res.add(sb.toString());
                }
                rs.close();
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
        List<Sql> sqls = bs.sqls;
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
        List<String> batchSqls = new ArrayList<>();
        int batchSize = this.r.nextInt(10);

        String pstmt = this.schema.genTablePreparedInsert(t);
        try (PreparedStatement stmt= this.conn.prepareStatement(pstmt)){
            for (int i = 0; i < batchSize; i++) {
                StringBuffer tmp = new StringBuffer("(");
                for (int j = 0; j < cols.size(); j++) {
                    String type = cols.get(j).getType();
                    Object val = generateColumnValueByType(type);
                    stmt.setObject(j + 1, val);
                    tmp.append(val);
                    if (j > 0) {
                        tmp.append(",");
                    }
                }
                tmp.append(")");
                batchSqls.add(replacePstmtWithValue(pstmt, tmp.toString()));
                stmt.addBatch();
            }
            int[] res = stmt.executeBatch();
            return new BatchResult(this.owner, batchSqls, res);
        } catch (SQLException e) {
            return new ErrorResult(this.owner, batchSqls, e.toString() + ", batch size: " + batchSize);
        }
    }

    private String replacePstmtWithValue(String pstmt, String value) {
        int bg = pstmt.indexOf("VALUES(");
        if (bg == -1) {
            throw new IllegalArgumentException("Illegal PrepareStmt: " + pstmt + ", Value String: " + value);
        }
        StringBuffer tmp = new StringBuffer(pstmt);
        tmp.delete(bg + 6, tmp.length());
        tmp.append(value);
        System.out.println("BATCH: " + tmp);
        return tmp.toString();
    }

    private Object generateColumnValueByType(String colType) {
        switch (colType) {
            case "DOUBLE":
            case "DECIMAL":
                return r.nextDouble();
            case "FLOAT":
                return r.nextFloat();
            case "INTEGER":
                return r.nextInt();
            case "BOOLEAN":
            case "BIT":
                return r.nextBoolean();
            default:
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&*()!.,;'\\";
                StringBuilder sb = new StringBuilder();
                int len = r.nextInt(50);
                for (int i = 0; i < len; i++) {
                    sb.append(characters.charAt(r.nextInt(characters.length())));
                }
                return sb.toString();
        }
    }

    private String getColumnValueByType(ResultSet rs, List<String> colType, int idx) throws SQLException {
        String type = colType.get(idx - 1);
        switch (type) {
            case "DOUBLE":
                return String.valueOf(rs.getDouble(idx));
            case "FLOAT":
                return String.valueOf(rs.getFloat(idx));
            case "DECIMAL":
                return String.valueOf(rs.getBigDecimal(idx));
            default:
                return rs.getString(idx);
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

    private void initConnAndSchema(String jdbc, String db, String config) throws SQLException {
        String url;
        if (config.startsWith("&")) {
            url = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s%s", jdbc, dbHost, dbPort, db, username, password, config);
        } else {
            url = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s&%s", jdbc, dbHost, dbPort, db, username, password, config);
        }
        logger.error("{} Connecting to {}", owner, url);
        this.conn = DriverManager.getConnection(url);
        this.schema = new GlobalSchema(this.conn, db, r);
    }

    public void closeConn() throws SQLException {
        this.conn.close();
    }

    public Conn(Random r, String jdbc, String db, String config) throws SQLException {
        this.r = r;
        this.owner = jdbc;
        this.db = db;
        initConnAndSchema(jdbc, db, config);
    }
}
