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
        List<String> batchSqls = new ArrayList<>();
        try (Statement stmt= this.conn.createStatement()){
            stmt.clearBatch();
            for (Sql s : sqls) {
                stmt.addBatch(s.sql);
                batchSqls.add(s.sql);
            }
            int[] res = stmt.executeBatch();
            return new BatchResult(this.owner, batchSqls, res);
        } catch (BatchUpdateException e) {
            int[] res = e.getUpdateCounts();
            return new BatchResult(this.owner, batchSqls, res);
        } catch (SQLException e) {
            return new ErrorResult(this.owner, batchSqls, e.toString());
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
            stmt.clearBatch();
            for (int i = 0; i < batchSize; i++) {
                StringBuffer tmp = new StringBuffer("(");
                for (int j = 0; j < cols.size(); j++) {
                    String type = cols.get(j).getType();
                    Object val = this.schema.generateColumnValueByType(type);
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
        } catch (BatchUpdateException e) {
            int[] res = e.getUpdateCounts();
            return new BatchResult(this.owner, batchSqls, res);
        } catch (SQLException e) {
            return new ErrorResult(this.owner, batchSqls, e.toString());
        }
    }

    public Result executeBatchInsert() throws SQLException {
        this.schema.renewTables();
        Table t = this.schema.getRandomTable();
        List<String> batchSqls = new ArrayList<>();
        int size = r.nextInt(10);
        try (Statement stmt = conn.createStatement()) {
            stmt.clearBatch();
            for (int i = 0; i < size; i++) {
                String tmp = this.schema.genTableRandomInsert(t);
                stmt.addBatch(tmp);
                batchSqls.add(tmp);
            }
            int[] res = stmt.executeBatch();
            return new BatchResult(this.owner, batchSqls, res);
        } catch (BatchUpdateException e) {
            int[] res = e.getUpdateCounts();
            return new BatchResult(this.owner, batchSqls, res);
        }  catch (SQLException e) {
            return new ErrorResult(this.owner, batchSqls, e.toString());
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

    protected String getColumnValueByType(ResultSet rs, List<String> colType, int idx) throws SQLException {
        return rs.getString(idx);
    }

    public void closeConn() throws SQLException {
        this.conn.close();
    }

    public Conn(Random r, String jdbc, String db, String config) throws SQLException {
        this.r = r;
        this.owner = jdbc;
        this.db = db;
    }
}
