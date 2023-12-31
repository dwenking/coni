package coni;


import coni.connector.input.BatchSql;
import coni.connector.input.Sql;
import coni.fuzzer.arg.ConfigArg;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfiguration {
    /**
     * Connection configurations
     */
    public static final String dbHost = "localhost";
    public static final String dbPort = "3366";
    public static final String iniDatabase = "test";
    public static final String username = "root";
    public static final String password = "123456";

    /**
     * Tested drivers configurations
     */
    public static final String maven = "/Users/dwenking/.m2/repository";
    public static final String project = "/Users/dwenking/Git/coni/jdbc";
    public static final String testDB = "mysql";
    public static final String jdbc1 = "mariadb";
    public static final String jdbc2 = "mysql";
    public static final String packagePrefix1 = "com/mysql";
    public static final String packagePrefix2 = "org/mariadb";

    /**
     * Server configurations
     */
    public static final String serverHost = "localhost";
    public static final int serverPort = 6300;

    /**
     * File configurations
     */
    public static final String logPath = "log/";
    public static final String seedPath = "src/main/resources/seeds/" + testDB;
    public static final String outPath = "out/";
    public static final String seedDelimiter = "@;";
}

