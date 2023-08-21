package coni.fuzzer.mutator;

import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;

import java.util.List;
import java.util.Random;

public class SwapMutator implements Mutator{
    private Random r;

    @Override
    public Seed mutate(Seed origin) {
        Seed copy = origin.deepCopy();
        List<FuncSeed> fc = copy.getFuncSeeds();
        int size = fc.size();
        if (size == 0) {
            return copy;
        }

        int idx1 = r.nextInt(4, size);
        int idx2 = r.nextInt(4, size);
        while (idx1 == idx2) {
            idx2 = r.nextInt(size);
        }

        FuncSeed temp = fc.get(idx1);
        fc.set(idx1, fc.get(idx2));
        fc.set(idx2, temp);
        return copy;
    }

    public SwapMutator(Random r) {
        this.r = r;
    }
}
