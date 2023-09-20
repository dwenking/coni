package coni.connector.schema;

import java.sql.Connection;
import java.util.Random;
import java.util.List;

public class MySQLGlobalSchema extends GlobalSchema{
    @Override
    public Object generateColumnValueByType(String colType) {
        switch (colType) {
            case "DOUBLE":
            case "DECIMAL":
                return r.nextDouble();
            case "FLOAT":
                return r.nextFloat();
            case "INT":
            case "INTEGER":
                return r.nextInt();
            case "BOOLEAN":
            case "BIT":
                return r.nextBoolean();
            case "VARCHAR":
            default:
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&*()!.,;'\\";
                StringBuilder sb = new StringBuilder();
                int len = r.nextInt(50);
                for (int i = 0; i < len; i++) {
                    sb.append(characters.charAt(r.nextInt(characters.length())));
                }
                return sb.toString();
        }
    }

    public MySQLGlobalSchema(Connection con, String db, Random r) {
        super(con, db, r);
    }
}
