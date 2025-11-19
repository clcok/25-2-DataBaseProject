package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.dto.User;
import main.java.util.DBManager;

public class UserDAO {

    /**
     * 로그인: ID와 PW로 회원 확인
     * (데이터를 가져올 때는 명확하게 YYYYMMDD 8자리로 가져오는 것이 좋습니다)
     */
    public User login(String userId, String password) {
        String sql = "SELECT User_id, Password, Name, Sex, TO_CHAR(Birth_date, 'YYYYMMDD') as BirthStr " +
                     "FROM USERS WHERE User_id = ? AND Password = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBManager.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                    rs.getString("User_id"),
                    rs.getString("Password"),
                    rs.getString("Name"),
                    rs.getString("Sex"), 
                    rs.getString("BirthStr"), // YYYYMMDD 포맷
                    null 
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return user;
    }

    /**
     * [수정됨] 비밀번호 찾기
     * - 사용자가 19710204(8자리)로 입력하든 710204(6자리)로 입력하든 모두 처리 가능하도록 개선
     */
    public String findPassword(String name, String id, String gender, String birthdate) {
        // 입력 길이에 따라 DB 포맷 결정
        String datePattern = "YYYYMMDD"; // 기본 8자리
        if (birthdate != null && birthdate.length() == 6) {
            datePattern = "YYMMDD";      // 6자리 입력 시
        }

        String sql = "SELECT Password FROM USERS " +
                     "WHERE Name = ? AND User_id = ? AND Sex = ? " +
                     "AND TO_CHAR(Birth_date, '" + datePattern + "') = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String foundPassword = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            pstmt.setString(3, gender);
            pstmt.setString(4, birthdate); 
            
            rs = pstmt.executeQuery();

            if (rs.next()) {
                foundPassword = rs.getString("Password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return foundPassword;
    }

    /**
     * 내 프로필 정보 조회 (LikeDAO, FollowDAO 연동)
     */
    public User getUserProfile(String userId) {
        // 조회는 YYYYMMDD로 통일
        String userSql = "SELECT User_id, Password, Name, Sex, TO_CHAR(Birth_date, 'YYYYMMDD') as BirthStr FROM USERS WHERE User_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBManager.getConnection();
            
            pstmt = conn.prepareStatement(userSql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new User(
                    rs.getString("User_id"),
                    rs.getString("Password"),
                    rs.getString("Name"),
                    rs.getString("Sex"),
                    rs.getString("BirthStr"),
                    null
                );
            }
            
            DBManager.close(conn, pstmt, rs);

            if (user != null) {
                LikeDAO likeDAO = new LikeDAO();
                int totalLikes = likeDAO.countReceivedLikes(userId);
                user.setTotalLikes(totalLikes);

                FollowDAO followDAO = new FollowDAO();
                int followers = followDAO.countFollowers(userId);
                int followings = followDAO.countFollowing(userId);
                
                user.setFollowerCount(followers);
                user.setFollowingCount(followings);
            }

        } catch (SQLException e) {
            System.err.println("프로필 조회 중 오류 발생");
            e.printStackTrace();
            DBManager.close(conn, pstmt, rs);
        }
        
        return user;
    }
    
    /**
     * 이름 수정
     */
    public boolean updateUserName(String userId, String newName) {
        String sql = "UPDATE USERS SET Name = ? WHERE User_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newName);
            pstmt.setString(2, userId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) success = true;
        } catch (SQLException e) {
            System.err.println("이름 수정 중 DB 오류");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt);
        }
        return success;
    }

    /**
     * 비밀번호 수정
     */
    public boolean updateUserPassword(String userId, String newPassword) {
        String sql = "UPDATE USERS SET Password = ? WHERE User_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) success = true;
        } catch (SQLException e) {
            System.err.println("비밀번호 수정 중 DB 오류");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt);
        }
        return success;
    }
    
    /**
     * 회원 정보 수정 (전체) - 사용자가 입력한 포맷에 맞춰 업데이트
     */
    public boolean updateUser(User user) {
        // 생년월일 길이에 따라 포맷 결정
        String datePattern = "YYYYMMDD";
        if (user.getBirthdate() != null && user.getBirthdate().length() == 6) {
            datePattern = "YYMMDD";
        }

        String sql = "UPDATE USERS SET Password = ?, Name = ?, Sex = ?, Birth_date = TO_DATE(?, '" + datePattern + "') " +
                     "WHERE User_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getSex());
            pstmt.setString(4, user.getBirthdate());
            pstmt.setString(5, user.getUserId());
            
            int result = pstmt.executeUpdate();
            if (result > 0) success = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt);
        }
        return success;
    }

    /**
     * 아이디 키워드 검색
     */
    public List<User> findByUserIdKeyword(String keyword) {
        List<User> result = new ArrayList<>();

        String sql = "SELECT User_id, Password, Name, Sex, TO_CHAR(Birth_date, 'YYYYMMDD') as BirthStr " +
                     "FROM USERS " +
                     "WHERE User_id LIKE '%' || ? || '%'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, keyword);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("User_id"),
                        rs.getString("Password"),
                        rs.getString("Name"),
                        rs.getString("Sex"),
                        rs.getString("BirthStr"),
                        null 
                );
                result.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }

        return result;
    }
}