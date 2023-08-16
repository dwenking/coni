package coni.connector.input;

import java.util.List;

/**
 * wrapper
 */
public class BatchSql {
    public Sql[] sqls;

    public BatchSql(String[] sqls) {
        Sql[] s = new Sql[sqls.length];
        for (int i = 0; i < sqls.length; i++) {
            s[i] = new Sql(sqls[i]);
        }
        this.sqls = s;
    }
    public BatchSql(Sql[] sqls) {
        this.sqls = sqls;
    }
}