package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.util.DBManager;

public class LikeDAO {

    /**
     * [추가됨] 좋아요 상태 확인 (변경하지 않고 조회만 함)
     * @return true(이미 좋아요 누름), false(안 누름)
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
     * @return true(좋아요 추가됨), false(좋아요 취소됨)
     */
    public boolean toggleLike(int postId, String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isLiked = false;

        try {
            conn = DBManager.getConnection();
            
            // 1. 이미 좋아요를 눌렀는지 확인
            String checkSql = "SELECT 1 FROM LIKES WHERE Post_id = ? AND User_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 2. 이미 존재함 -> 삭제 (좋아요 취소)
                DBManager.close(null, pstmt, rs); 
                
                String deleteSql = "DELETE FROM LIKES WHERE Post_id = ? AND User_id = ?";
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.setInt(1, postId);
                pstmt.setString(2, userId);
                pstmt.executeUpdate();
                
                isLiked = false; 
            } else {
                // 3. 없음 -> 삽입 (좋아요 추가)
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