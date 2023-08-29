package coni.fuzzer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Seed {
    private List<FuncSeed> funcSeeds;

    public List<FuncSeed> getFuncSeeds() {
        return funcSeeds;
    }

    public Seed(List<FuncSeed> funcSeeds) {
        this.funcSeeds = funcSeeds;
    }

    public Seed deepCopy() {
        List<FuncSeed> copy = new LinkedList<>();
        for (FuncSeed fs : this.funcSeeds) {
            copy.add(fs.deepCopy());
        }
        return new Seed(copy);
    }

    public boolean hasConfig() {
        return !this.funcSeeds.isEmpty() && this.funcSeeds.get(0).getMethod().contains("config");
    }

    public boolean hasMethod() {
        if (this.funcSeeds.isEmpty()) {
            return false;
        }
        if (this.funcSeeds.size() == 1 && hasConfig()) {
            return false;
        }
        return true;
    }

    public int getRandomMethodIdx(Random r) {
        int size = this.funcSeeds.size();
        if (size == 0 || (size == 1 && hasConfig()) || size <= 4) {
            return -1;
        }
        return r.nextInt(4, size);
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();
        for (FuncSeed s : funcSeeds) {
            tmp.append(s.toString());
            tmp.append("\n");
        }
        if (!tmp.isEmpty()) {
            tmp.deleteCharAt(tmp.length() - 1);
        }
        return tmp.toString();
    }
}
