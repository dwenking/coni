package coni.connector.result;

import java.util.List;
import java.util.Objects;

public class UpdateResult extends Result{
    private int num;

    @Override
    public void print() {
        System.out.println(this.owner + " changed " + this.num + " rows");
    }

    @Override
    public void printInfo() {
        logger.info(this.owner + " changed " + this.num + " rows");
    }

    @Override
    public void printError() {
        logger.error(this.owner + " changed " + this.num + " rows");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateResult result = (UpdateResult) o;
        return this.num == result.num && this.sqls.equals(result.sqls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner, this.num, this.sqls);
    }

    public UpdateResult(String owner, List<String> sqls, int num) {
        super(owner, sqls);
        this.num = num;
    }
}
