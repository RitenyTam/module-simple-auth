import java.sql.*;

public class TestSqlLite {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        //SQLite 数据库文件
        String dbFile = "D:\\workspace\\databases\\identifier.sqlite";
        String url = "jdbc:sqlite:" + dbFile;
        Connection conn = DriverManager.getConnection(url);

        select(conn);

        conn.close();
    }

    private static void select(Connection connection) throws SQLException {
        String sql = "select * from user";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password");

            System.out.println(username);
            System.out.println(password);
        }
        rs.close();
        statement.close();
    }
}
