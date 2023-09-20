package coni.auto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class RandomStatement extends RandomClass{
    static Class<?> cls = Statement.class;
    static Map<String, Method> methodDict = new HashMap<String, Method>();
    static Logger logger = LogManager.getLogger("RandomStatement");
    static String[][] stagedMethod = {{"setMaxFieldSize", "setQueryTimeout", "setMaxRows"},
            {"addBatch", "clearBatch"},
            {"execute", "executeQuery", "executeUpdate", "executeLargeUpdate", "executeBatch", "executeLargeBatch"},
            {"getResultSet", "getUpdateCount", "getMoreResults", "getLargeUpdateCount", "getGeneratedKeys"},
            {"close", "cancle"}};

    public static void main(String[] args) throws SQLException, InvocationTargetException, IllegalAccessException {
        init();
        String url = "jdbc:mysql://localhost:3366/test?user=root&password=123456";
        Connection con = DriverManager.getConnection(url);
        Statement stmt = con.createStatement();
        setProperties(stmt, stagedMethod[0]);
    }


    public static void setProperties(Statement stmt, String[] propertyMethods) throws InvocationTargetException, IllegalAccessException {
        for (String propertyMethod : propertyMethods) {
            Method method = methodDict.get(propertyMethod);
            Object[] arguments = generateParams(method);
            logger.info("set {} value: {}", propertyMethod, Arrays.toString(arguments));
        }
    }

    private static Object[] generateParams(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            if (paramType == int.class) {
                arguments[i] = (int) (Math.random() * 100);
            } else if (paramType == String.class) {
                arguments[i] = generateRandomSQL();
            } else {
                throw new IllegalArgumentException("Invalid parameter type");
            }
        }
        return arguments;
    }

    private static String generateRandomSQL() {
        return "DROP DATABASE IF EXISTS test;";
    }

    private static void init() {
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            methodDict.put(method.getName(), method);
        }
    }
}
