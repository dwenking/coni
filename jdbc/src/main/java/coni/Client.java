package coni;

import coni.connector.result.Result;
import coni.executor.Executor;
import coni.executor.Seed;
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

/**
 * invoke by server, exec a seed
 */
public class Client {
    private static final Logger logger = LogManager.getLogger("Client");

    public static void main(String[] args) {
        if (args.length == 2) {
            List<Seed> seeds = FileUtil.readSeedsFromString(args[0]);
            String id = args[1];
            long s = System.currentTimeMillis();
            Executor exec1 = null, exec2 = null;

            try {
                if (seeds.size() > 0 && "config".equals(seeds.get(0).getMethod())) {
                    String config = seeds.get(0).getArgs().get(0).toString();
                    exec1 = new Executor(new Random(s), jdbc1, iniDatabase, config);
                    exec2 = new Executor(new Random(s), jdbc2, iniDatabase, config);
                } else {
                    exec1 = new Executor(new Random(s), jdbc1, iniDatabase, "");
                    exec2 = new Executor(new Random(s), jdbc2, iniDatabase, "");
                }
            } catch (SQLException e) {
                logger.error("Connecting to 0 {} failed, e:{}", iniDatabase, e.getMessage());
                return;
            }

            List<Result> res1 = new ArrayList<>();
            List<Result> res2 = new ArrayList<>();
            for (Seed seed : seeds) {
                if ("config".equals(seed.getMethod())) {
                    continue;
                }
                res1.add(exec1.exec(seed));
            }
            for (Seed seed : seeds) {
                if ("config".equals(seed.getMethod())) {
                    continue;
                }
                res2.add(exec2.exec(seed));
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
                    logger.error("Writing seed succeed");
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
