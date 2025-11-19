package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FollowDAO {

    // 팔로워 수: 나를 팔로우하는 사람 몇 명인가
    public int countFollowers(String userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM FOLLOW WHERE Following_id = ?";
        int cnt = 0;

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("cnt");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnt;
    }

    // 팔로잉 수: 내가 몇 명을 팔로우하고 있는가
    public int countFollowing(String userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM FOLLOW WHERE Follower_id = ?";
        int cnt = 0;

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("cnt");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnt;
    }

    public void follow(String me, String target) {
        String sql = "INSERT INTO FOLLOW (Follower_id, Following_id) VALUES (?, ?)";

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, me);
            pstmt.setString(2, target);

            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unfollow(String me, String target) {
        String sql = "DELETE FROM FOLLOW WHERE Follower_id = ? AND Following_id = ?";

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, me);
            pstmt.setString(2, target);

            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isIFollow(String me, String target) {
        String sql = "SELECT COUNT(*) AS cnt FROM FOLLOW " +
                "WHERE Follower_id = ? AND Following_id = ?";
        int cnt = 0;

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, me);      // 내가
            pstmt.setString(2, target);  // 상대를

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("cnt");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnt > 0;
    }

    public boolean isHeFollowsMe(String me, String target) {
        String sql = "SELECT COUNT(*) AS cnt FROM FOLLOW " +
                "WHERE Follower_id = ? AND Following_id = ?";
        int cnt = 0;

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, target);  // 상대가
            pstmt.setString(2, me);      // 나를

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt("cnt");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnt > 0;
    }

}
