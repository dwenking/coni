package coni.fuzzer;

import coni.fuzzer.arg.*;

import java.util.LinkedList;
import java.util.List;

import static coni.GlobalConfiguration.seedDelimiter;
import static coni.fuzzer.Dict.methodDict;


public class FuncSeed {
    private final String method;
    private List<Arg> args;

//    public static FuncSeed getRandomFuncSeed(String method) {
//        FuncSeed r = new FuncSeed(method);
//        if (!methodDict.containsKey(method)) {
//            throw new IllegalArgumentException("Illegal Seeds " +  method);
//        }
//        Class<?>[] argType = methodDict.get(method);
//        for (int i = 0; i < argType.length; i++) {
//            r.getArgs().add(ArgFactory.createRandom(argType[i]));
//        }
//        return r;
//    }

    public FuncSeed(String[] origin) throws IllegalArgumentException{
        this.method = origin[0];
        Class<?>[] argType = methodDict.get(method);
        this.args = new LinkedList<>();

        if (method.contains("config") && origin.length == 1) {
            this.args.add(ArgFactory.create(argType[0]));
            return;
        }

        if (origin == null || origin.length < 1 ||
                !methodDict.containsKey(origin[0]) || argType.length != origin.length - 1) {
            throw new IllegalArgumentException("Illegal Seeds " + origin[0] + "...");
        }

        for (int i = 0; i < argType.length; i++) {
            this.args.add(ArgFactory.create(argType[i], origin[i + 1]));
        }
    }

    public FuncSeed(String method) {
        this.method = method;
        this.args = new LinkedList<>();
    }

    public FuncSeed deepCopy() {
        FuncSeed copy = new FuncSeed(method);
        Class<?>[] argType = methodDict.get(method);

        for (int i = 0; i < argType.length; i++) {
            copy.getArgs().add(ArgFactory.create(argType[i], this.args.get(i).toString()));
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();
        tmp.append(this.method);
        tmp.append(seedDelimiter);
        for (Arg a : args) {
            tmp.append(a.toString());
            tmp.append(seedDelimiter);
        }
        tmp.delete(tmp.lastIndexOf(seedDelimiter), tmp.length());
        return tmp.toString();
    }

    public String getMethod() {
        return method;
    }

    public List<Arg> getArgs() {
        return args;
    }
}
