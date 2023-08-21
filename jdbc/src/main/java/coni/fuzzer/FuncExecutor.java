package coni.fuzzer;

import coni.connector.conn.Conn;
import coni.connector.result.Result;
import coni.fuzzer.arg.Arg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * execute funcseed
 */
public class FuncExecutor {
    private Conn conn;

    public Result exec(FuncSeed funcSeed) {
       try {
           String method = funcSeed.getMethod();
           Result res = (Result) this.exec(conn, method, funcSeed.getArgs());
           return res;
       } catch (InvocationTargetException e) {
           throw new RuntimeException(e);
       } catch (NoSuchMethodException e) {
           throw new RuntimeException(e);
       } catch (IllegalAccessException e) {
           throw new RuntimeException(e);
       }
    }

    public Object exec(Object owner, String methodName, List<Arg> args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] types = args.stream()
                .map(Arg::getType)
                .toArray(Class<?>[]::new);
        Object[] values = args.stream()
                .map(Arg::getValue)
                .toArray(Object[]::new);

        Method method = owner.getClass().getMethod(methodName, types);
        Object res = method.invoke(owner, values);
        return res;
    }

    public FuncExecutor(Random r, String owner, String db, String config) throws SQLException {
        this.conn = new Conn(r, owner, db, config);
    }
}
