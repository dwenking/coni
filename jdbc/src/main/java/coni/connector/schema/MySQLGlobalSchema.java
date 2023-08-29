package coni.connector.schema;

import java.sql.Connection;
import java.util.Random;

public class MySQLGlobalSchema extends GlobalSchema{
    public MySQLGlobalSchema(Connection con, String db, Random r) {
        super(con, db, r);
    }
}
