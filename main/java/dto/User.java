package main.java.dto;

import java.sql.Timestamp;

public class User {
    
    // 테이블 컬럼과 매핑되는 필드
    private String userId;
    private String password;
    private String name;
    private String sex;        // DDL의 'Sex' 컬럼 (M/F)
    private String birthdate;  // DDL의 'Birth_date' 컬럼 (YYMMDD 문자열 처리)
    private Timestamp createdAt; // 가입일 (DDL에는 없지만 로직상 필요할 수 있어 유지, null 가능)

    // 프로필 조회 시 사용할 추가 필드 (조인 결과 저장용)
    private int totalLikes;
    private int followerCount;
    private int followingCount;

    // 기본 생성자 (UserDAO에서 조회 시 사용)
    public User(String userId, String password, String name, String sex, String birthdate, Timestamp createdAt) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.birthdate = birthdate;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 프로필 통계 정보 Getters/Setters
    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}