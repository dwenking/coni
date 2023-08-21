package coni.connector.input;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

/**
 * wrapper
 */
public class BatchSql {
    public List<Sql> sqls;
    public String all;

    public BatchSql(String[] sqls) {
        List<Sql> s = new ArrayList<>();
        StringBuffer all = new StringBuffer();
        for (int i = 0; i < sqls.length; i++) {
            s.add(new Sql(sqls[i]));
            all.append(sqls[i]);
            all.append(";");
        }
        if (!all.isEmpty()) {
            all.deleteCharAt(all.length() - 1);
        }
        this.all = all.toString();
        this.sqls = s;
    }
    public BatchSql(List<Sql> sqls) {
        this.sqls = sqls;
    }

    @Override
    public String toString() {
        return all;
    }
}