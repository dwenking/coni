package coni.executor;

import coni.connector.conn.Conn;
import coni.connector.result.Result;
import coni.executor.arg.SqlArg;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * execute with seeds
 */
public class Executor {
    private Conn conn;

    public Result exec(Seed seed) {
       try {
           String method = seed.getMethod();
           Func f = new Func(conn, method);
           Result res = (Result) f.exec(seed.getArgs());
           return res;
       } catch (InvocationTargetException e) {
           throw new RuntimeException(e);
       } catch (NoSuchMethodException e) {
           throw new RuntimeException(e);
       } catch (IllegalAccessException e) {
           throw new RuntimeException(e);
       }
    }

    public Executor(Random r, String owner, String db) throws SQLException {
        this.conn = new Conn(r, owner, db);
    }
}
