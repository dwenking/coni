package coni.executor;

import coni.connector.input.BatchSql;
import coni.connector.input.Sql;
import coni.executor.arg.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Seed {
    private String method;
    private List<Arg> args;

    private static Map<String, Class<?>[]> methodDict = new HashMap<>();

    {
        methodDict.put("config", new Class<?>[]{ConfigArg.class});
        methodDict.put("execute", new Class<?>[]{Sql.class});
        methodDict.put("executeBatch", new Class<?>[]{BatchSql.class});
        methodDict.put("executeBatchPreparedInsert", new Class<?>[]{});
    }

    public String getMethod() {
        return method;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public Seed(String[] origin) throws IllegalArgumentException{
        this.method = origin[0];
        Class<?>[] argType = methodDict.get(method);
        if (origin == null || origin.length < 1 ||
                !methodDict.containsKey(origin[0]) || argType.length != origin.length - 1) {
            throw new IllegalArgumentException("Illegal Seeds " + origin[0]);
        }

        this.args = new ArrayList<>();
        for (int i = 0; i < argType.length; i++) {
            this.args.add(ArgFactory.create(argType[i], origin[i + 1]));
        }
    }
}
