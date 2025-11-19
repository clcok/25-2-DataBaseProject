package main.java.dao; // ★ 패키지 수정됨

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ★ import 경로 수정됨
import main.java.dto.Post;
import main.java.util.DBManager;

public class PostDAO {

    // --- 공통 SQL 조각 (목록 조회용) ---
    private final String BASE_SELECT = 
        "SELECT p.Post_id, p.Content, p.Created_at, p.User_id, " +
        "  (SELECT COUNT(*) FROM LIKES l WHERE l.Post_id = p.Post_id) as LikeCount, " +
        "  NULL as RepImage " + 
        "FROM POST p ";

    /**
     * [추가됨] 특정 유저(나)의 게시물 목록 조회
     */
    public List<Post> findPostsByUserId(String userId) {
        String sql = BASE_SELECT + 
                     "WHERE p.User_id = ? " +
                     "ORDER BY p.Created_at DESC";
        
        return getPostsByQuery(sql, userId);
    }

    /**
     * 1. 추천 게시물 조회 (가중치 기반 알고리즘 적용)
     */
    public List<Post> findRecommendedPosts(String userId) {
        String sql = 
            "WITH UserPrefs AS ( " +
            "    SELECT l.Label_name, COUNT(*) as Weight " +
            "    FROM LIKES k " +
            "    JOIN IMAGE i ON k.Post_id = i.Post_id " +
            "    JOIN LABEL l ON i.Image_dir = l.Image_dir " +
            "    WHERE k.User_id = ? " +
            "    GROUP BY l.Label_name " +
            "), " +
            "PostScores AS ( " +
            "    SELECT p.Post_id, SUM(up.Weight) as TotalScore " +
            "    FROM POST p " +
            "    JOIN IMAGE i ON p.Post_id = i.Post_id " +
            "    JOIN LABEL l ON i.Image_dir = l.Image_dir " +
            "    JOIN UserPrefs up ON l.Label_name = up.Label_name " +
            "    WHERE p.User_id != ? " +
            "    GROUP BY p.Post_id " +
            ") " +
            "SELECT p.Post_id, p.Content, p.Created_at, p.User_id, " +
            "       (SELECT COUNT(*) FROM LIKES l WHERE l.Post_id = p.Post_id) as LikeCount, " +
            "       NULL as RepImage " +
            "FROM POST p " +
            "JOIN PostScores ps ON p.Post_id = ps.Post_id " +
            "ORDER BY ps.TotalScore DESC, p.Created_at DESC";

        return getPostsByQuery(sql, userId, userId);
    }

    public List<Post> findFollowingPosts(String userId) {
        String sql = BASE_SELECT + 
                     "JOIN FOLLOW f ON p.User_id = f.Following_id " +
                     "WHERE f.Follower_id = ? " +
                     "ORDER BY p.Created_at DESC";
        
        return getPostsByQuery(sql, userId);
    }

    public List<Post> findPostsByHashtag(String tagName) {
        String sql = BASE_SELECT + 
                     "JOIN HASHTAG h ON p.Post_id = h.Post_id " +
                     "WHERE h.Hashtag_name = ? " +
                     "ORDER BY p.Created_at DESC";
        
        return getPostsByQuery(sql, tagName);
    }
    
    public Post findPostById(int postId) {
        String sql = 
            "SELECT p.Post_id, p.Content, p.Created_at, p.User_id, " +
            "  (SELECT COUNT(*) FROM LIKES l WHERE l.Post_id = p.Post_id) as LikeCount, " +
            "  (SELECT Image_dir FROM IMAGE i WHERE i.Post_id = p.Post_id AND ROWNUM = 1) as RepImage " + 
            "FROM POST p " +
            "WHERE p.Post_id = ?";
        
        List<Post> result = getPostsByQuery(sql, String.valueOf(postId));
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean uploadPost(String userId, String content, String imageDir, List<String> hashtags) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            int newPostId = 0;
            String seqSql = "SELECT NVL(MAX(Post_id), 0) + 1 FROM POST";
            pstmt = conn.prepareStatement(seqSql);
            rs = pstmt.executeQuery();
            if (rs.next()) newPostId = rs.getInt(1);
            DBManager.close(null, pstmt, rs);

            String insertPost = "INSERT INTO POST (Post_id, Content, Created_at, User_id) VALUES (?, ?, SYSTIMESTAMP, ?)";
            pstmt = conn.prepareStatement(insertPost);
            pstmt.setInt(1, newPostId);
            pstmt.setString(2, content);
            pstmt.setString(3, userId);
            pstmt.executeUpdate();
            DBManager.close(null, pstmt);

            if (imageDir != null && !imageDir.trim().isEmpty()) {
                String insertImg = "INSERT INTO IMAGE (Image_dir, Post_id) VALUES (?, ?)";
                pstmt = conn.prepareStatement(insertImg);
                pstmt.setString(1, imageDir);
                pstmt.setInt(2, newPostId);
                pstmt.executeUpdate();
                DBManager.close(null, pstmt);
            }

            if (hashtags != null && !hashtags.isEmpty()) {
                String insertTag = "INSERT INTO HASHTAG (Hashtag_id, Hashtag_name, Post_id) " + 
                                   "VALUES ((SELECT NVL(MAX(Hashtag_id), 0) + 1 FROM HASHTAG), ?, ?)";
                pstmt = conn.prepareStatement(insertTag);
                for (String tag : hashtags) {
                    pstmt.setString(1, tag);
                    pstmt.setInt(2, newPostId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
            DBManager.close(conn, pstmt);
        }
        return success;
    }

    private List<Post> getPostsByQuery(String sql, String... params) {
        List<Post> posts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                try {
                    pstmt.setString(i + 1, params[i]);
                } catch (Exception e) {
                    pstmt.setString(i + 1, params[i]);
                }
            }
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Post p = new Post(
                    rs.getInt("Post_id"),
                    rs.getString("Content"),
                    rs.getTimestamp("Created_at"),
                    rs.getString("User_id"),
                    rs.getString("RepImage"),
                    rs.getInt("LikeCount")
                );
                posts.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return posts;
    }
}