package coni.executor.arg;

import coni.connector.input.BatchSql;
import coni.connector.input.Sql;


public class ArgFactory {
    public static Arg create(Class<?> type, String value) throws IllegalArgumentException{
        if (type.equals(Sql.class)) {
            return new SqlArg(value);
        } else if (type.equals(BatchSql.class)) {
            return new BatchSqlArg(value.split(";"));
        } else if (type.equals(ConfigArg.class)) {
            return new ConfigArg(value);
        }
        else {
            throw new IllegalArgumentException("Illegal Arg Type: " + type + ", " + value);
        }
    }
}
