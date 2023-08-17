package coni.executor.arg;

import coni.connector.input.Config;
import coni.connector.input.Sql;

public class ConfigArg extends Arg{
    public ConfigArg(String value) {
        super(ConfigArg.class, new Config(value));
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public Arg mutate() {
        return this;
    }
}
