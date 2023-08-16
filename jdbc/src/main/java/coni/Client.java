package coni;

import coni.connector.result.Result;
import coni.executor.Executor;
import coni.executor.Seed;
import coni.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static coni.GlobalConfiguration.*;

/**
 * exec a seed file and store the result
 */
public class Client {
    private static String SEED_PATH = "src/main/resources/seeds/";
    private static final Logger logger = LogManager.getLogger("Client");

    public static void main(String[] args) {
        if (args.length == 1) {
            String fileName = args[0];
            List<Seed> seeds = FileUtil.readSeedsFromFile(SEED_PATH + fileName);

            if (seeds.size() == 0) {
                return;
            }

            long s = System.currentTimeMillis();
            Executor exec1 = null, exec2 = null;
            try {
                exec1 = new Executor(new Random(s), jdbc1, iniDatabase);
                exec2 = new Executor(new Random(s), jdbc2, iniDatabase);
            } catch (SQLException e) {
                logger.error("Connecting to database %s failed.\n", iniDatabase);
                return;
            }

            List<Result> res1 = new ArrayList<>();
            List<Result> res2 = new ArrayList<>();
            for (Seed seed : seeds) {
                res1.add(exec1.exec(seed));
            }
            for (Seed seed : seeds) {
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
            }
        }
        else {
            logger.error("Invalid args, can not process size " + args.length);
        }
    }
}
