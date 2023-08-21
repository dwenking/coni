package coni.fuzzer.mutator;

import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;

import java.util.List;
import java.util.Random;

public class DelMutator implements Mutator{
    private Random r;

    @Override
    public Seed mutate(Seed origin) {
        Seed copy = origin.deepCopy();
        List<FuncSeed> fc = copy.getFuncSeeds();
        int size = fc.size();
        if (size == 0) {
            return copy;
        }

        int idx = r.nextInt(4, size);
        fc.remove(idx);
        return copy;
    }

    public DelMutator(Random r) {
        this.r = r;
    }
}
