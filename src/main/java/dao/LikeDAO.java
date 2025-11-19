package main.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LikeDAO {

    // 특정 유저가 받은 좋아요 총 개수
    public int countLikesReceived(String userId) {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM POST P
            JOIN LIKES L ON P.Post_id = L.Post_id
            WHERE P.User_id = ?
            """;

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
