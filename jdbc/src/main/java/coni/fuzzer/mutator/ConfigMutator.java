package coni.fuzzer.mutator;

import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;
import coni.fuzzer.arg.Arg;
import coni.fuzzer.arg.ConfigArg;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static coni.fuzzer.Dict.*;

public class ConfigMutator implements Mutator{
    private Random r;
    /**
     * 0: add
     * 1: remove
     * 2: change
     */
    private static int mutateStrategy = 3;

    @Override
    public Seed mutate(Seed origin) {
        Seed copy = origin.deepCopy();

        if (copy.hasConfig()) {
            List<FuncSeed> fs = copy.getFuncSeeds();
            mutate(fs.get(0));
        } else {
            throw new IllegalArgumentException("Config mutator failed for empty config settings");
        }

        return copy;
    }

    private void mutate(FuncSeed origin) {
        int strategy = r.nextInt(mutateStrategy);
        List<Arg> args = origin.getArgs();

        if (strategy == 0 || args.isEmpty() || args.get(0).toString().isEmpty()) {
            add(origin);
        } else if (strategy == 1) {
            remove(origin);
        } else if (strategy == 2) {
            change(origin);
        } else {
            throw new RuntimeException("Config Mutator failed, strategy number: " + strategy);
        }
    }

    private void add(FuncSeed origin) {
        String method = origin.getMethod();

        List<Arg> args = origin.getArgs();
        String config = args.get(0).toString();
        args.remove(0);
        StringBuffer tmp = new StringBuffer(config);

        if (!config.endsWith("&")) {
            tmp.append("&");
        }
        tmp.append(getRandomConfigFromDict(r, method));

        args.add(new ConfigArg(tmp.toString()));
    }

    private void remove(FuncSeed origin) {
        List<Arg> args = origin.getArgs();
        String config = args.get(0).toString();
        String[] configs = config.split("&");
        if (configs.length == 0) {
            return;
        }

        args.remove(0);
        int rmIdx = r.nextInt(configs.length);
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < configs.length; i++) {
            if (i != rmIdx){
                tmp.append(configs[i]);
                tmp.append("&");
            }
        }
        if (!tmp.isEmpty()) {
            tmp.deleteCharAt(tmp.length() - 1);
        }

        args.add(new ConfigArg(tmp.toString()));
    }

    private void change(FuncSeed origin) {
        String method = origin.getMethod();
        List<Arg> args = origin.getArgs();
        String config = args.get(0).toString();
        String[] configs = config.split("&");
        if (configs.length == 0) {
            return;
        }
        args.remove(0);

        int changeIdx = r.nextInt(configs.length);
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < configs.length; i++) {
            if (i != changeIdx){
                tmp.append(configs[i]);
            } else {
                tmp.append(getRandomConfigFromDict(r, method));
            }
            tmp.append("&");
        }
        if (!tmp.isEmpty()) {
            tmp.deleteCharAt(tmp.length() - 1);
        }

        args.add(new ConfigArg(tmp.toString()));
    }

    public ConfigMutator(Random r) {
        this.r = r;
    }
}