package coni;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import coni.connector.input.BatchSql;
import coni.connector.input.Sql;
import coni.fuzzer.Seed;
import coni.fuzzer.arg.ConfigArg;
import coni.fuzzer.mutator.MutExecutor;
import coni.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.runtime.RemoteControlReader;

import static coni.GlobalConfiguration.*;
import static coni.fuzzer.Dict.initDict;

/**
 * fuzz entry
 */
public final class Server {
    private static final Logger logger = LogManager.getLogger("Server");
    private static int[] covData = new int[4];

    public static void main(final String[] args) throws IOException {
        final ServerSocket server = new ServerSocket(serverPort, 0,
                InetAddress.getByName(serverHost));
        File seedFolder = new File(seedPath);
        initDict();
        MutExecutor mutator = new MutExecutor();
        List<Seed> que = new ArrayList<>();

        for (File f : seedFolder.listFiles()) {
            que.add(FileUtil.readSeedsFromFile(f.getPath()));
        }

        //long fuzzTime = 120000;
        //long targetTime = System.currentTimeMillis() + fuzzTime;
        int epoch = 0;
        int id = 0;
        logger.info("Start Fuzzing...");

        while (!que.isEmpty() && epoch < 5) {
            List<Seed> cur = null;
            if (epoch == 0) {
                cur = que;
            }
            else {
                cur = mutator.mutate(que);
            }
            for (Seed s : cur) {
                startProcess(s.toString(), id++);
                final Handler handler = new Handler(server.accept(), covData);
                boolean update = handler.updateCov();
                if (update) {
                    logger.info("Epoch {}, Coverage update: {} connector {} / {}, {} connector {} / {}", epoch, jdbc1, covData[0], covData[1], jdbc2, covData[2], covData[3]);
                    if (epoch != 0) {
                        que.add(s);
                    }
                }
            }
            epoch++;
        }

        FileUtil.resetFuzzLog(logPath, "fuzz");
    }

    private static void startProcess(String seed, int id) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "bash",
                "-c",
                "export PROJECT_PATH=" + project + " && " +
                        "export MAVEN_PATH=" + maven + " && " +
                        "java -javaagent:${PROJECT_PATH}/jar/jacocoagent.jar=sessionid=" + id + ",output=tcpclient,address=127.0.0.1,port=6300,includes=\"org.mariadb.*:com.mysql.*\" " +
                        "-classpath ${MAVEN_PATH}/org/mariadb/jdbc/mariadb-java-client/3.1.3/mariadb-java-client-3.1.3.jar:${MAVEN_PATH}/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar:${PROJECT_PATH}/target/classes:${MAVEN_PATH}/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar:${MAVEN_PATH}/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar " +
                        "coni.Client \"" + seed + "\"" + " \"" + id + "\""
        );
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.error("Process " + id + " exit...");
            } else {
                logger.error("Process " + id + " fail...");
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Process " + id + " fail...");
        }
    }

    private static class Handler
            implements ISessionInfoVisitor, IExecutionDataVisitor {

        private final Socket socket;
        private final RemoteControlReader reader;
        private int[] covData;
        private int cur1 = 0, sum1 = 0, cur2 = 0, sum2 = 0;
        private boolean hasSum = false;

        Handler(final Socket socket, int[] covData)
                throws IOException {
            this.socket = socket;
            this.covData = covData;
            this.reader = new RemoteControlReader(socket.getInputStream());
            this.reader.setSessionInfoVisitor(this);
            this.reader.setExecutionDataVisitor(this);
        }

        public boolean updateCov() {
            try {
                while (reader.read()) {
                }
                socket.close();
                boolean update = false;
                if ((long)cur1 * covData[2] > (long)covData[0] * sum1) {
                    update = true;
                    covData[0] = cur1;
                    covData[2] = sum1;
                }
                if ((long)cur2 * covData[3] > (long)covData[1] * sum2) {
                    update = true;
                    covData[1] = cur2;
                    covData[3] = sum2;
                }
                return update;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public void visitSessionInfo(final SessionInfo info) {
        }

        public void visitClassExecution(final ExecutionData data) {
            String name = data.getName();
            if (name.startsWith(packagePrefix1)) {
                cur1 += Integer.valueOf(getHitCount(data.getProbes()));
                sum1 += data.getProbes().length;
            }
            else if (name.startsWith(packagePrefix2)){
                cur2 += Integer.valueOf(getHitCount(data.getProbes()));
                sum2 += data.getProbes().length;
            }
        }

        private int getHitCount(final boolean[] data) {
            int count = 0;
            for (final boolean hit : data) {
                if (hit) {
                    count++;
                }
            }
            return count;
        }
    }

    public Server() {
    }
}