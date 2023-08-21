package coni.connector.result;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Store return data
 */
public abstract class Result {
    protected String owner;
    protected List<String> sqls;
    protected String all;
    protected static final Logger logger = LogManager.getLogger("Result");

    public void print(String level) {
        switch (level.toLowerCase()) {
            case "info":
                printInfo();
                break;
            case "error":
                printError();
                break;
            default:
                print();
                break;
        }
    }

    public void printSql(String level) {
        if (this.sqls != null) {
            switch (level.toLowerCase()) {
                case "info":
                    logger.info("Execute: {}", all);
                    break;
                case "error":
                    logger.error("Execute: {}", all);
                    break;
                default:
                    System.out.println("Execute: " + all);
                    break;
            }
        }
    }

    public abstract void print();
    public abstract void printInfo();
    public abstract void printError();

    public Result(String owner, List<String> sqls) {
        this.owner = owner;
        this.sqls = sqls;
        StringBuffer all = new StringBuffer();
        for (String sql : sqls) {
            all.append(sql);
            all.append(",");
        }
        if (!all.isEmpty()) {
            all.deleteCharAt(all.length() - 1);
        }
        this.all = all.toString();
    }
}
