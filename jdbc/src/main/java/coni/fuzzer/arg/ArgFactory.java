package coni.fuzzer.arg;

import coni.connector.input.BatchSql;
import coni.connector.input.Config;
import coni.connector.input.Sql;

/**
 * Create Arg according to its type
 */
public class ArgFactory {
    public static Arg create(Class<?> type, String value) throws IllegalArgumentException{
        if (type.equals(Sql.class)) {
            return new SqlArg(value);
        } else if (type.equals(BatchSql.class)) {
            return new BatchSqlArg(value.split(";"));
        } else if (type.equals(Config.class)) {
            return new ConfigArg(value);
        }
        else {
            throw new IllegalArgumentException("Illegal Arg Type: " + type + ", " + value);
        }
    }
}
