package main.java.util; // ★ 패키지명이 중요합니다!

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {
    
    // ====================================================
    // [TODO] 본인의 오라클 DB 정보로 반드시 수정해주세요!
    // ====================================================
    private static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
    
    // localhost 대신 IP주소를 적어야 할 수도 있습니다. (예: 192.168.0.X)
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    
    private static final String DB_USER = "teamp"; // 아이디
    private static final String DB_PASSWORD = "comp322"; // 비밀번호
    // ====================================================

    // 클래스 로딩 시 드라이버 로드 (1회)
    static {
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패! ojdbc 라이브러리가 추가되었는지 확인하세요.");
            e.printStackTrace();
        }
    }

    /**
     * DB 연결 객체 반환
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("데이터베이스 연결 실패! ID/PW나 URL을 확인하세요.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 자원 해제 (SELECT 용)
     */
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 자원 해제 (INSERT, UPDATE, DELETE 용)
     */
    public static void close(Connection conn, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}