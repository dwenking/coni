package coni.fuzzer.arg;

import coni.connector.input.BatchSql;

public class BatchSqlArg extends Arg{

    public BatchSqlArg(String[] value) {
        super(BatchSql.class, new BatchSql(value));
    }

    @Override
    public Arg mutate() {
        return this;
    }
}