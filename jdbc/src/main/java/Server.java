import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.runtime.RemoteControlReader;

import static coni.GlobalConfiguration.*;

public final class Server {

    public static String SEED_PATH = "src/main/resources/seeds/";
    private static final String ADDRESS = "localhost";
    private static final int PORT = 6300;
    private static final Logger logger = LogManager.getLogger("Server");
    private static AtomicInteger diffCount = new AtomicInteger(0);
    private static int[] cov = new int[2];

    public static void main(final String[] args) throws IOException {
        final ServerSocket server = new ServerSocket(PORT, 0,
                InetAddress.getByName(ADDRESS));

        File seedFolder = new File(SEED_PATH);
        Queue<String> que = new LinkedList<>();

        for (File f : seedFolder.listFiles()) {
            que.add(f.getName());
        }

        long fuzzTime = 120000;
        long targetTime = System.currentTimeMillis() + fuzzTime;
        logger.info("Start Fuzzing...");

        while (!que.isEmpty() && System.currentTimeMillis() < targetTime) {
            String cur = que.poll();
            logger.info("Start Process " + cur + "...");
            startProcess(cur);
            final Handler handler = new Handler(server.accept(), cov);
            boolean update = handler.updateCov();
            if (update) {
                logger.info("Coverage update: {} connector {}, {} connector {}", jdbc1, cov[0], jdbc2, cov[1]);
            }
        }
    }

    private static void startProcess(String seedFile) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "bash",
                "-c",
                "export PROJECT_PATH=" + project + " && " +
                        "export MAVEN_PATH=" + maven + " && " +
                        "java -javaagent:${PROJECT_PATH}/jar/jacocoagent.jar=sessionid=" + seedFile + ",output=tcpclient,address=127.0.0.1,port=6300,includes=\"org.mariadb.*:com.mysql.*\" " +
                        "-classpath ${PROJECT_PATH}/jar/mariadb-java-client-3.1.3.jar:${PROJECT_PATH}/jar/mysql-connector-j-8.0.33.jar:${PROJECT_PATH}/target/classes:${MAVEN_PATH}/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar:${MAVEN_PATH}/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar " +
                        "coni.Client " + seedFile
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
                logger.info("Process " + seedFile + " exit...");
            } else {
                logger.info("Process " + seedFile + " fail...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Handler
            implements ISessionInfoVisitor, IExecutionDataVisitor {

        private final Socket socket;
        private final RemoteControlReader reader;
        private int[] cov;
        private int cur1 = 0, cur2 = 0;

        Handler(final Socket socket, int[] cov)
                throws IOException {
            this.socket = socket;
            this.cov = cov;
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
                if (cur1 > cov[0] || cur2 > cov[1]) {
                    update = true;
                    cov[0] = Integer.max(cur1, cov[0]);
                    cov[1] = Integer.max(cur2, cov[1]);
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
            }
            else if (name.startsWith(packagePrefix2)){
                cur2 += Integer.valueOf(getHitCount(data.getProbes()));
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