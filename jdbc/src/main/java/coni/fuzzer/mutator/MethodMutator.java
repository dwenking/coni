package coni.fuzzer.mutator;

import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;

import java.util.List;
import java.util.Random;

import static coni.fuzzer.Dict.getRandomMethodFromDict;

public class MethodMutator implements Mutator{
    private Random r;
    /**
     * 0: create
     * 1: add
     * 2: remove
     * 3: swap
     */
    private static int mutateStrategy = 4;

    @Override
    public Seed mutate(Seed origin) {
        Seed copy = origin.deepCopy();
        int strategy = r.nextInt(mutateStrategy);

        if (strategy == 0 || !copy.hasMethod()) {
            create(copy);
        }
        else if (strategy == 1) {
            add(copy);
        } else if (strategy == 2) {
            remove(copy);
        } else if (strategy == 3) {
            swap(copy);
        }
        return copy;
    }

    private void create(Seed s) {
        FuncSeed create = new FuncSeed(getRandomMethodFromDict(r));
        List<FuncSeed> fs = s.getFuncSeeds();
        fs.add(create);
    }

    private void add(Seed s) {
        if (!s.hasMethod()) {
            return;
        }
        List<FuncSeed> fs = s.getFuncSeeds();
        int idx = s.getRandomMethodIdx(r);

        FuncSeed ad = fs.get(idx).deepCopy();
        fs.add(s.getRandomMethodIdx(r), ad);
    }

    private void remove(Seed s) {
        if (!s.hasMethod()) {
            return;
        }

        List<FuncSeed> fs = s.getFuncSeeds();
        int idx = s.getRandomMethodIdx(r);
        fs.remove(idx);
    }

    private void swap(Seed s) {
        if (!s.hasMethod()) {
            return;
        }
        List<FuncSeed> fs = s.getFuncSeeds();

        int idx1 = s.getRandomMethodIdx(r);
        int idx2 = s.getRandomMethodIdx(r);
        if (idx1 == idx2) {
            idx2 = s.getRandomMethodIdx(r);
        }

        FuncSeed temp = fs.get(idx1);
        fs.set(idx1, fs.get(idx2));
        fs.set(idx2, temp);
    }

    public MethodMutator(Random r) {
        this.r = r;
    }
}
