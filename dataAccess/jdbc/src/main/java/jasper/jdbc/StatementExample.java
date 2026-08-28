package jasper.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public class StatementExample {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 打开连接
            String url = "jdbc:mysql://localhost:3307/learn?useSSL=false&serverTimezone=UTC";
            String user = "root";
            String password = "passwd";
            conn = DriverManager.getConnection(url, user, password);

            // 执行查询
            stmt = conn.createStatement();
            String sql = "SELECT id, nickname FROM user";
            rs = stmt.executeQuery(sql);

            // 展开结果集
            while (rs.next()) {
                int id = rs.getInt("id");
                String nickname = rs.getString("nickname");
                System.out.println("ID: " + id + ", nickname: " + nickname);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
            } catch (Exception ignored) {
            };
            try {
                if (stmt != null) stmt.close();
            } catch (Exception ignored) {
            };
            try {
                if (conn != null) conn.close();
            } catch (Exception ignored) {
            };
        }
    }
}