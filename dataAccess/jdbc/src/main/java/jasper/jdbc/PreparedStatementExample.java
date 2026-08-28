package jasper.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class PreparedStatementExample {

    public static void main(String[] args) {
        // 数据库URL、用户名和密码
        String url = "jdbc:mysql://localhost:3307/learn";
        String user = "root";
        String password = "passwd";

        // SQL查询语句，包含两个参数占位符
        String query = "SELECT * FROM user WHERE status = ? AND  deleted = ?";

        int  status = 1;
        int deleted = 0;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 1. 获取数据库连接
            conn = DriverManager.getConnection(url, user, password);

            // 2. 创建PreparedStatement对象
            pstmt = conn.prepareStatement(query);

            // 3. 设置SQL语句中的参数值
            pstmt.setInt(1, status); // 第一个问号的位置，从1开始计数
            pstmt.setInt(2, deleted);      // 第二个问号的位置

            // 4. 执行查询并获取结果集
            rs = pstmt.executeQuery();

            // 5. 处理结果集
            while (rs.next()) {
                System.out.println("User ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("nickname"));
            }
        } catch (SQLException e) {
            log.info(e.getMessage());
        } finally {
            // 6. 关闭资源
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }
}