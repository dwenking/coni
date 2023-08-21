package coni.fuzzer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
