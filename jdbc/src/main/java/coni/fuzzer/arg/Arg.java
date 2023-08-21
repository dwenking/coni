package coni.fuzzer.arg;

import coni.fuzzer.FuncSeed;

/**
 * Store value and type info
 */
public class Arg {
    private final Class<?> type;
    private Object value;

    public Arg mutate(){return this;};

    public Arg(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
