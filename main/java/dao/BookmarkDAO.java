package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import main.java.dto.Post;
import main.java.util.DBManager;

public class BookmarkDAO {

    // 특정 유저가 북마크한 게시물 목록 조회
    public List<Post> findBookmarkedPosts(String userId) {
        List<Post> list = new ArrayList<>();

        String sql = 
            "SELECT p.Post_id, p.Content, p.Created_at, p.User_id, " +
            "  (SELECT COUNT(*) FROM LIKES l WHERE l.Post_id = p.Post_id) as LikeCount, " +
            "  NULL as RepImage " + 
            "FROM POST p " +
            "JOIN BOOKMARK b ON p.Post_id = b.Post_id " +
            "WHERE b.User_id = ? " +
            "ORDER BY b.Bookmark_id DESC"; 

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("Post_id"),
                        rs.getString("Content"),
                        rs.getTimestamp("Created_at"),
                        rs.getString("User_id"),
                        rs.getString("RepImage"),
                        rs.getInt("LikeCount")
                );
                list.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }

        return list;
    }

    // 북마크 여부 확인
    public boolean checkBookmark(int postId, String userId) {
        boolean isBookmarked = false;
        String sql = "SELECT 1 FROM BOOKMARK WHERE Post_id = ? AND User_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                isBookmarked = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return isBookmarked;
    }

    // 북마크 추가 (시퀀스 없이 구현)
    public boolean addBookmark(int postId, String userId) {
        // [수정됨] 시퀀스 대신 MAX(ID) + 1 사용
        String sql = "INSERT INTO BOOKMARK (Bookmark_id, Post_id, User_id) " +
                     "VALUES ((SELECT NVL(MAX(Bookmark_id), 0) + 1 FROM BOOKMARK), ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, userId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt);
        }
        return success;
    }

    // 북마크 취소
    public boolean removeBookmark(int postId, String userId) {
        String sql = "DELETE FROM BOOKMARK WHERE Post_id = ? AND User_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, userId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt);
        }
        return success;
    }
}