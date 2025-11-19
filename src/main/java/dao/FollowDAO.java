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

}
