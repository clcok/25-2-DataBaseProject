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

    // [기존 기능 유지] 로그인
    public User login(String userId, String password) {
        String sql = "SELECT User_id, Password, Name, Sex, TO_CHAR(Birth_date, 'YYMMDD') as BirthStr " +
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
                    rs.getString("User_id"), rs.getString("Password"),
                    rs.getString("Name"), rs.getString("Sex"), 
                    rs.getString("BirthStr"), null 
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return user;
    }

    // [기존 기능 유지] 비밀번호 찾기
    public String findPassword(String name, String id, String gender, String birthdate) {
        String sql = "SELECT Password FROM USERS WHERE Name = ? AND User_id = ? AND Sex = ? AND TO_CHAR(Birth_date, 'YYMMDD') = ?";
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
            if (rs.next()) foundPassword = rs.getString("Password");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return foundPassword;
    }

    // [기존 기능 유지] 프로필 조회
    public User getUserProfile(String userId) {
        String userSql = "SELECT User_id, Password, Name, Sex, TO_CHAR(Birth_date, 'YYMMDD') as BirthStr FROM USERS WHERE User_id = ?";
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
                    rs.getString("User_id"), rs.getString("Password"),
                    rs.getString("Name"), rs.getString("Sex"),
                    rs.getString("BirthStr"), null
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

    // [수정됨] 이름 변경 전용 메소드
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

    // [수정됨] 비밀번호 변경 전용 메소드
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

    // [기존 기능 유지] 아이디 검색
    public List<User> findByUserIdKeyword(String keyword) {
        List<User> result = new ArrayList<>();
        String sql = "SELECT User_id, Password, Name, Sex, TO_CHAR(Birth_date, 'YYMMDD') as BirthStr FROM USERS WHERE User_id LIKE '%' || ? || '%'";

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
                        rs.getString("User_id"), rs.getString("Password"),
                        rs.getString("Name"), rs.getString("Sex"),
                        rs.getString("BirthStr"), null
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