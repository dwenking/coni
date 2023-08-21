package coni.fuzzer.mutator;

import coni.fuzzer.Seed;

public interface Mutator {
    public Seed mutate(Seed origin);
}
