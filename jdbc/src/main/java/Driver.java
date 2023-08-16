import java.sql.*;

public class Driver {
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 加载JDBC驱动程序
            Class.forName("org.mariadb.jdbc.Driver");

            // 连接数据库
            String url = "jdbc:mariadb://localhost:3366/test";
            String user = "root";
            String password = "123456";
            conn = DriverManager.getConnection(url, user, password);

            // 执行SQL语句
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS t0(id INT, name VARCHAR(255));");

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM t0");

            // 处理查询结果
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接和资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
