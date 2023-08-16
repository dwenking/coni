package coni.executor.arg;

import coni.connector.input.BatchSql;
import coni.connector.input.Sql;

import java.util.Arrays;

public class BatchSqlArg extends Arg{

    public BatchSqlArg(String[] value) {
        super(BatchSql.class, new BatchSql(value));
    }

    @Override
    public Arg mutate() {
        return this;
    }
}