package coni.util;

import coni.fuzzer.FuncSeed;
import coni.fuzzer.Seed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static coni.GlobalConfiguration.seedDelimiter;

public class FileUtil {
    private static final Logger logger = LogManager.getLogger("FileUtil");

    public static Seed readSeedsFromString(String input) throws IllegalArgumentException{
        List<FuncSeed> funcSeeds = new LinkedList<>();

        if (input.isEmpty()) {
            return new Seed(funcSeeds);
        }

        String[] lines = input.split("\n");
        for (String line : lines) {
            String[] tmp = line.split(seedDelimiter);
            funcSeeds.add(new FuncSeed(tmp));
        }
        return new Seed(funcSeeds);
    }

    public static Seed readSeedsFromFile(String file) {
        List<FuncSeed> funcSeeds = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tmp = line.split(seedDelimiter);
                try {
                    funcSeeds.add(new FuncSeed(tmp));
                } catch (IllegalArgumentException e) {
                    logger.error("Seed {} has wrong format", line);
                }
            }
        } catch (IOException e) {
            logger.error("Read {} error", file);
        }
        return new Seed(funcSeeds);
    }

    public static void resetFuzzLog(String path, String folderName) {
        File[] files = new File(path).listFiles();
        int fileCount = files.length;

        File folder = new File(path + folderName);

        if (folder.exists() && folder.isDirectory()) {
            String newFolderName = path + folderName + (fileCount - 1);
            File newFolder = new File(newFolderName);

            if (folder.renameTo(newFolder)) {
                logger.info("Log reset success");
            } else {
                logger.error("Log reset failed");
            }
        } else {
            logger.error("No log files");
        }
    }

    public static List<String> generateSQLFromFile(String fileContent) {
        int bg = 0;
        boolean funcFlag = false, strFlag = false;
        char strStart = '\'';
        List<String> res = new ArrayList<>();
        fileContent = fileContent.strip();

        for (int i = 0; i < fileContent.length(); i++) {
            if (fileContent.charAt(i) == ';' && !funcFlag && !strFlag) {
                String tmp = fileContent.substring(bg, i).strip();
                bg = i + 1;
                // 注释不要
                if (!tmp.startsWith("--") || tmp.contains("\n")) {
                    res.add(tmp);
                }
            }
            // STRING
            else if (fileContent.charAt(i) == '\'' || fileContent.charAt(i) == '\"') {
                if (!strFlag) {
                    strFlag = true;
                    strStart = fileContent.charAt(i);
                }
                else if (fileContent.charAt(i) == strStart && strFlag) {
                    strFlag = false;
                }
            }
            // BEGIN END
            else if (!strFlag && fileContent.charAt(i) == 'B' && i + 5 <= fileContent.length() && "BEGIN".equalsIgnoreCase(fileContent.substring(i, i + 5))) {
                funcFlag = true;
                // 跳到最后一个字符
                i += 4;
            }
            else if (!strFlag && fileContent.charAt(i) == 'E' && i + 3 <= fileContent.length() && "END".equalsIgnoreCase(fileContent.substring(i, i + 3))) {
                funcFlag = false;
                // 跳到最后一个字符
                i += 2;
            }
        }

        if (bg + 1 < fileContent.length()) {
            // 注释不要
            String tmp = fileContent.substring(bg).strip();
            if (!tmp.startsWith("--") && tmp.contains("\n")) {
                res.add(tmp);
            }
        }

        return res;
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static List<String> listFiles(String startDir, boolean useRelativeFPath) {
        if(useRelativeFPath) {
            return listFilesInner(startDir, "");
        }else {
            return listFilesInner(startDir, Paths.get(startDir).toAbsolutePath().toString());
        }
    }

    private static List<String> listFilesInner(String startDir, String prefixFPath) {
        File dir = new File(startDir);
        File[] files = dir.listFiles();
        List<String> ans = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (File file : files) {
                var fPath = prefixFPath.length()==0?file.getName(): (prefixFPath + File.separator + file.getName());
                if (file.isDirectory()) {
                    ans.addAll(listFilesInner(startDir + File.separator + file.getName(), fPath));
                } else {
                    ans.add(fPath);
                }
            }
        }
        return ans;
    }
}
