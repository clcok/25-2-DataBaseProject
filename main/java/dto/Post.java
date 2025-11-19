package main.java.dto;

import java.sql.Timestamp;

public class Post {
    
    private int postId;
    private String content;  // CLOB 타입은 String으로 처리
    private Timestamp createdAt;
    private String userId;   // 작성자
    
    // DB 테이블에는 없지만, 화면 출력을 위해 조인해서 가져올 필드들
    private String imageDir; // IMAGE 테이블에서 가져옴 (썸네일용)
    private int likeCount;   // LIKES 테이블 count
    
    // 생성자
    public Post(int postId, String content, Timestamp createdAt, String userId, String imageDir, int likeCount) {
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.imageDir = imageDir;
        this.likeCount = likeCount;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getImageDir() { return imageDir; }
    public void setImageDir(String imageDir) { this.imageDir = imageDir; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
}
