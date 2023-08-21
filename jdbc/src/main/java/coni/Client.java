package coni;

import coni.connector.result.Result;
import coni.fuzzer.FuncExecutor;
import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;
import coni.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static coni.GlobalConfiguration.*;
import static coni.fuzzer.Dict.initDict;

/**
 * invoke by server, exec a seed
 */
public class Client {
    private static final Logger logger = LogManager.getLogger("Client");

    public static void main(String[] args) {
        if (args.length == 2) {
            //System.out.println(args[0]);
            initDict();
            Seed se = FileUtil.readSeedsFromString(args[0]);
            List<FuncSeed> seed = se.getFuncSeeds();
            String id = args[1];
            FuncExecutor exec1 = null, exec2 = null;

            try {
                if (seed.size() > 0 && "config".equals(seed.get(0).getMethod())) {
                    String config = seed.get(0).getArgs().get(0).toString();
                    exec1 = new FuncExecutor(new Random(Long.parseLong(id)), jdbc1, iniDatabase, config);
                    exec2 = new FuncExecutor(new Random(Long.parseLong(id)), jdbc2, iniDatabase, config);
                } else {
                    exec1 = new FuncExecutor(new Random(Long.parseLong(id)), jdbc1, iniDatabase, "");
                    exec2 = new FuncExecutor(new Random(Long.parseLong(id)), jdbc2, iniDatabase, "");
                }
            } catch (SQLException e) {
                logger.error("Connecting to 0 {} failed, e:{}", iniDatabase, e.getMessage());
                return;
            }

            logger.info("Process {} start...", id);
            List<Result> res1 = new ArrayList<>();
            List<Result> res2 = new ArrayList<>();
            for (FuncSeed funcSeed : seed) {
                if ("config".equals(funcSeed.getMethod())) {
                    continue;
                }
                res1.add(exec1.exec(funcSeed));
            }
            for (FuncSeed funcSeed : seed) {
                if ("config".equals(funcSeed.getMethod())) {
                    continue;
                }
                res2.add(exec2.exec(funcSeed));
            }

            boolean isDifferent = false;
            for (int i = 0; i < res1.size(); i++) {
                Result r1 = res1.get(i), r2 = res2.get(i);

                if (!r1.equals(r2)) {
                    isDifferent = true;
                    r1.printSql("error");
                    r1.print("error");
                    r2.print("error");
                }
                else {
                    r1.printSql("info");
                    r1.print("info");
                    r2.print("info");
                }
            }

            if (isDifferent) {
                try (FileWriter writer = new FileWriter(outPath + id)) {
                    writer.write(args[0]);
                    logger.info("Writing seed succeed");
                } catch (IOException e) {
                    logger.error("Writing seed to {} fail, e:{}", outPath + id, e.getMessage());
                }
            }
        }
        else {
            logger.error("Invalid args, can not process size {}", args.length);
        }
    }
}
