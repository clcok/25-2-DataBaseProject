package main.java.dto;

import java.sql.Timestamp;

public class Comment {
    
    private int commentId;
    private String content;
    private Timestamp createdAt;
    private String userId;
    private int postId;
    private Integer parentCommentId; // 부모 댓글 ID (대댓글일 경우), 없으면 null

    public Comment(int commentId, String content, Timestamp createdAt, String userId, int postId, Integer parentCommentId) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }
    
    // --- Getters (App.java에서 사용하는 메소드들) ---

    public int getCommentId() { 
        return commentId; 
    }

    public String getContent() { 
        return content; 
    }

    public Timestamp getCreatedAt() { 
        return createdAt; 
    }

    public String getUserId() { 
        return userId; 
    }

    public int getPostId() { 
        return postId; 
    }

    public Integer getParentCommentId() { 
        return parentCommentId; 
    }
}