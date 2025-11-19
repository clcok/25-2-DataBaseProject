package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.dto.Comment;
import main.java.util.DBManager;

public class CommentDAO {

    /**
     * 특정 게시물의 댓글 목록 조회
     */
    public List<Comment> findCommentsByPostId(int postId) {
        // [수정됨] 댓글 정렬 방식 변경 (계층형 보기)
        // 1. NVL(Parent_comment_id, Comment_id): 부모와 자식을 같은 그룹(부모ID)으로 묶습니다.
        // 2. Comment_id: 그룹 내에서 순서대로(부모 -> 자식) 정렬합니다.
        String sql = "SELECT * FROM COMMENTS WHERE Post_id = ? " +
                     "ORDER BY NVL(Parent_comment_id, Comment_id) ASC, Comment_id ASC";
        
        List<Comment> comments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int commentId = rs.getInt("Comment_id");
                String content = rs.getString("Text"); 
                String userId = rs.getString("User_id");
                
                int parentIdVal = rs.getInt("Parent_comment_id");
                Integer parentId = rs.wasNull() ? null : parentIdVal;

                Comment c = new Comment(
                    commentId,
                    content, 
                    rs.getTimestamp("Created_at"),
                    userId,
                    postId,
                    parentId
                );
                comments.add(c);
            }
        } catch (SQLException e) {
            System.err.println("댓글 조회 중 오류 발생");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt, rs);
        }
        return comments;
    }

    /**
     * 댓글 작성 (시퀀스 없이 구현)
     */
    public boolean addComment(Comment comment) {
        String sql = "INSERT INTO COMMENTS (Comment_id, Text, User_id, Post_id, Parent_comment_id, Created_at) " +
                     "VALUES ((SELECT NVL(MAX(Comment_id), 0) + 1 FROM COMMENTS), ?, ?, ?, ?, SYSTIMESTAMP)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, comment.getContent()); 
            pstmt.setString(2, comment.getUserId());
            pstmt.setInt(3, comment.getPostId());
            
            if (comment.getParentCommentId() == null || comment.getParentCommentId() == 0) {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(4, comment.getParentCommentId());
            }
            
            int result = pstmt.executeUpdate();
            if (result > 0) success = true;

        } catch (SQLException e) {
            System.err.println("댓글 작성 중 오류 발생");
            e.printStackTrace();
        } finally {
            DBManager.close(conn, pstmt);
        }
        return success;
    }
}