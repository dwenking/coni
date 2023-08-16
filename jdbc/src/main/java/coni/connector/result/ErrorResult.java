package coni.connector.result;

import java.util.List;
import java.util.Objects;

public class ErrorResult extends Result{
    private String error;
    private String message;
    private static final String[] ignoreExceptions = new String[]{"com.mysql.cj.jdbc.exceptions.MysqlDataTruncation"};

    @Override
    public void print() {
        System.out.println(this.owner + " error: " + this.message);
    }

    @Override
    public void printInfo() {
        logger.info(this.owner + " error: " + this.message);
    }

    @Override
    public void printError() {
        logger.error(this.owner + " error: " + this.message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorResult result = (ErrorResult) o;

        if (this.sqls.equals(result.sqls)) {
            for (String excp : ignoreExceptions) {
                if (excp.equals(result.error) || excp.equals(this.error)) {
                    return true;
                }
            }
            return this.error.equals(result.error);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner, this.error, this.message, this.sqls);
    }

    public ErrorResult(String owner, List<String> sqls, String message) {
        super(owner, sqls);
        this.error = message.split(":")[0].toLowerCase();
        this.message = message;
    }
}
