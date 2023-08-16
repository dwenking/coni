package coni;


public class GlobalConfiguration {
    /**
     * Configurations for driver connecting to database
     */
    public static final String host = "localhost";
    public static final String port = "3366";
    public static final String iniDatabase = "test";
    public static final String username = "root";
    public static final String password = "123456";
    /**
     * Configurations for tested drivers
     */
    public static final String jdbc1 = "mariadb";
    public static final String jdbc2 = "mysql";
    public static final String packagePrefix1 = "com/mysql";
    public static final String packagePrefix2 = "org/mariadb";
    public static final String maven = "/Users/dwenking/.m2/repository";
    public static final String project = "/Users/dwenking/Git/coni/jdbc";}
