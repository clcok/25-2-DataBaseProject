package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.util.DBManager;

public class LikeDAO {

    /**
     * [추가됨] 특정 유저가 쓴 게시물들이 받은 '총 좋아요 수' 계산
     */
    public int countReceivedLikes(String userId) {
        String sql = "SELECT COUNT(*) FROM LIKES l " + 
                     "JOIN POST p ON l.Post_id = p.Post_id " + 
                     "WHERE p.User_id = ?";
        int count = 0;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("좋아요 수 집계 중 오류");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return count;
    }

    /**
     * 좋아요 상태 확인 (변경하지 않고 조회만 함)
     */
    public boolean checkLike(int postId, String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isLiked = false;

        try {
            conn = DBManager.getConnection();
            String checkSql = "SELECT 1 FROM LIKES WHERE Post_id = ? AND User_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                isLiked = true;
            }
        } catch (SQLException e) {
            System.err.println("좋아요 상태 확인 중 오류");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return isLiked;
    }

    /**
     * 좋아요 토글 (있으면 삭제, 없으면 추가)
     */
    public boolean toggleLike(int postId, String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isLiked = false;

        try {
            conn = DBManager.getConnection();
            
            String checkSql = "SELECT 1 FROM LIKES WHERE Post_id = ? AND User_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                DBManager.close(null, pstmt, rs); 
                String deleteSql = "DELETE FROM LIKES WHERE Post_id = ? AND User_id = ?";
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.setInt(1, postId);
                pstmt.setString(2, userId);
                pstmt.executeUpdate();
                isLiked = false; 
            } else {
                DBManager.close(null, pstmt, rs); 
                String insertSql = "INSERT INTO LIKES (Post_id, User_id) VALUES (?, ?)";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setInt(1, postId);
                pstmt.setString(2, userId);
                pstmt.executeUpdate();
                isLiked = true;
            }

        } catch (SQLException e) {
            System.err.println("좋아요 처리 중 오류 발생");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        
        return isLiked;
    }
}