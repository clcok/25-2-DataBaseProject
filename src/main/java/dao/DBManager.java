package main.java.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String URL =
            "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
    private static final String USER = "db_project";
    private static final String PASSWORD = "myoracle";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver"); // 드라이버 로드
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // DB 연결 반환
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
