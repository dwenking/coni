package coni.connector.result;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class NormalResult extends Result{
    private List<String> sortedResult;

    @Override
    public void print() {
        System.out.println(this.owner + " result：");
        for (String str : this.sortedResult) {
            System.out.println(str);
        }
    }

    @Override
    public void printInfo() {
        logger.info(this.owner + " result：");
        for (String str : this.sortedResult) {
            logger.info(str);
        }
    }

    @Override
    public void printError() {
        logger.error(this.owner + " result：");
        for (String str : this.sortedResult) {
            logger.error(str);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NormalResult result = (NormalResult) o;

        if (result.sortedResult.size() == this.sortedResult.size() && this.sqls.equals(result.sqls)) {
            for (int i = 1; i < result.sortedResult.size(); i++) {
                if (!result.sortedResult.get(i).equals(this.sortedResult.get(i))) {
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner, this.sortedResult, this.sqls);
    }

    public NormalResult(String owner, List<String> sqls, List<String> result) {
        super(owner, sqls);
        String type = result.get(0);
        result.remove(0);
        result.sort(Comparator.naturalOrder());
        result.add(0, type);
        this.sortedResult = result;
    }
}
