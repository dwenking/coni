package coni.fuzzer.mutator;

import coni.fuzzer.Seed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MutExecutor {
    Mutator[] mutators;
    Random r;

    public Seed mutate(Seed origin) {
        Mutator selected = mutators[r.nextInt(mutators.length)];
        return selected.mutate(origin);
    }

    public List<Seed> mutate(List<Seed> origin) {
        List<Seed> mut = new ArrayList<>();
        for (Seed s : origin) {
            mut.add(this.mutate(s));
        }
        return mut;
    }

    public MutExecutor() {
        this.r = new Random();
        this.mutators = new Mutator[]{new ConfigMutator(r), new MethodMutator(r)};
    }
}
