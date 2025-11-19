package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.dto.User;
import main.java.util.DBManager;

public class UserDAO {

    /**
     * 실제 DB에서 ID와 PW를 확인하여 로그인 처리
     */
    public User login(String userId, String password) {
        String sql = "SELECT User_id, Password, Name, Sex, Birth_date FROM USERS WHERE User_id = ? AND Password = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 로그인 성공! DB에서 가져온 정보로 User 객체 생성
                // DDL 상 USERS 테이블에 'Created_at' 컬럼이 없으므로 마지막 인자는 null 처리
                user = new User(
                    rs.getString("User_id"),
                    rs.getString("Password"),
                    rs.getString("Name"),
                    rs.getString("Sex"), // DDL 컬럼명: Sex
                    rs.getString("Birth_date"),
                    null // CreatedAt (USERS 테이블에 없어서 null 처리)
                );
            }
        } catch (SQLException e) {
            System.err.println("로그인 DB 조회 중 오류 발생");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        
        return user;
    }

    // 나머지 기능은 아직 구현 안 함 (null 리턴)
    public String findPassword(String name, String id, String gender, String birthdate) { return null; }
    public User getUserProfile(String userId) { return null; }
    public boolean updateUser(User user) { return false; }
}