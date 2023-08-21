package coni.fuzzer.arg;

import coni.connector.input.Config;

public class ConfigArg extends Arg{
    public ConfigArg(String value) {
        super(ConfigArg.class, new Config(value));
    }

    @Override
    public Arg mutate() {
        return this;
    }
}
