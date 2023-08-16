package coni.connector.result;

import java.util.List;
import java.util.Objects;

public class BatchResult extends Result{
    private String res = "";

    @Override
    public void print() {
        System.out.println(this.owner + " batch execute result：" + this.res);
    }

    @Override
    public void printInfo() {
        logger.info(this.owner + " batch execute result：" + this.res);
    }

    @Override
    public void printError() {
        logger.error(this.owner + " batch execute result：" + this.res);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BatchResult result = (BatchResult) o;

        return this.res.equals(result.res) && this.sqls.equals(result.sqls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner, this.res, this.sqls);
    }

    public BatchResult(String owner, List<String> sqls, int[] batchRes) {
        super(owner, sqls);

        StringBuffer tmp = new StringBuffer();
        for (int res : batchRes) {
            tmp.append(res);
            tmp.append("*");
        }
        if (tmp.length() > 0) {
            tmp.deleteCharAt(tmp.length() - 1);
        }
        this.res = tmp.toString();
    }
}
