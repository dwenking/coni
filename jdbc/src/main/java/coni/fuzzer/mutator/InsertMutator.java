package coni.fuzzer.mutator;

import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;

import java.util.List;
import java.util.Random;

public class InsertMutator implements Mutator{
    private Random r;

    @Override
    public Seed mutate(Seed origin) {
        Seed copy = origin.deepCopy();
        List<FuncSeed> fs = copy.getFuncSeeds();
        int insertIdx = r.nextInt(fs.size());
        // fs.add(insertIdx, generateRandomFuncSeed());
        return copy;
    }

//    public FuncSeed generateRandomFuncSeed() {
//        int index = r.nextInt(methodDict.size());
//        String method = methodDict.keySet().toArray(new String[0])[index];
//        return FuncSeed.getRandomFuncSeed(method);
//    }

    public InsertMutator(Random r) {
        this.r = r;
    }
}
