package coni.connector.schema;

import java.sql.Connection;
import java.util.Random;

public class PostgresGlobalSchema extends GlobalSchema{
    public PostgresGlobalSchema(Connection con, String db, Random r) {
        super(con, db, r);
    }
}
