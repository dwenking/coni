package coni.fuzzer.arg;


import coni.connector.input.Sql;

public class SqlArg extends Arg{
    public SqlArg(String value) {
        super(Sql.class, new Sql(value));
    }

    @Override
    public Arg mutate() {
        return this;
    }
}


