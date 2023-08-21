package coni.fuzzer;

import coni.connector.input.BatchSql;
import coni.connector.input.Config;
import coni.connector.input.Sql;
import coni.fuzzer.arg.ConfigArg;

import java.util.HashMap;
import java.util.Map;

public class Dict {
    public static Map<String, Class<?>[]> methodDict = new HashMap<>();

    public static void initDict() {
        methodDict.put("config", new Class<?>[]{Config.class});
        methodDict.put("execute", new Class<?>[]{Sql.class});
        methodDict.put("executeBatch", new Class<?>[]{BatchSql.class});
        methodDict.put("executeBatchPreparedInsert", new Class<?>[]{});
    }
}
