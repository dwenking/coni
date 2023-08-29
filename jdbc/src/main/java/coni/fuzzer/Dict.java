package coni.fuzzer;

import coni.connector.input.BatchSql;
import coni.connector.input.Config;
import coni.connector.input.Sql;
import coni.fuzzer.arg.ConfigArg;

import java.util.*;

public class Dict {
    public static Map<String, Class<?>[]> methodDict = new HashMap<>();
    public static Set<String> randomMethod = new HashSet<>();
    public static Map<String, String[]> mysqlConfigDict = new HashMap<>();
    public static Map<String, String[]> postgresConfigDict = new HashMap<>();

    public static void initDict() {
        methodDict.put("mysqlconfig", new Class<?>[]{Config.class});
        methodDict.put("pgconfig", new Class<?>[]{Config.class});
        methodDict.put("execute", new Class<?>[]{Sql.class});
        methodDict.put("executeBatch", new Class<?>[]{BatchSql.class});
        methodDict.put("executeBatchPreparedInsert", new Class<?>[]{});

        randomMethod.add("executeBatchPreparedInsert");

        String[] boolArr = new String[]{"true", "false"};
        mysqlConfigDict.put("allowMultiQueries", boolArr);
        mysqlConfigDict.put("useServerPrepStmts", boolArr);
        mysqlConfigDict.put("allowLocalInfile", boolArr);
        mysqlConfigDict.put("dumpQueriesOnException", boolArr);
        mysqlConfigDict.put("useCompression", boolArr);
        mysqlConfigDict.put("tinyInt1isBit", boolArr);
        mysqlConfigDict.put("yearIsDateType", boolArr);
        mysqlConfigDict.put("createDatabaseIfNotExist", boolArr);
        mysqlConfigDict.put("cacheCallableStmts", boolArr);
        mysqlConfigDict.put("cachePrepStmts", boolArr);
        mysqlConfigDict.put("rewriteBatchedStatements", boolArr);

        postgresConfigDict.put("allowEncodingChanges", boolArr);
        postgresConfigDict.put("reWriteBatchedInserts", boolArr);
        postgresConfigDict.put("disableColumnSanitiser", boolArr);
    }

    public static String getRandomMethodFromDict(Random r) {
        String[] methods = randomMethod.toArray(new String[0]);
        String method = methods[r.nextInt(methods.length)];
        return method;
    }

    public static String getRandomConfigFromDict(Random r, String method) {
        Map<String, String[]> configDict = getConfigDict(method);
        StringBuffer res = new StringBuffer();
        String[] keys = configDict.keySet().toArray(String[]::new);
        String key = keys[r.nextInt(keys.length)];
        String[] values = configDict.get(key);
        String value = values[r.nextInt(values.length)];
        res.append(key);
        res.append("=");
        res.append(value);
        return res.toString();
    }

    private static Map<String, String[]> getConfigDict(String method) {
        if ("mysqlconfig".equals(method)) {
            return mysqlConfigDict;
        } else if ("pgconfig".equals(method)) {
            return postgresConfigDict;
        } else {
            throw new IllegalArgumentException("Illegal config method: " + method);
        }
    }
}
