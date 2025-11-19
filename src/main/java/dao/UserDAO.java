package main.java.dao;

import main.java.dto.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class UserDAO {

    // 아이디 키워드 검색
    public List<User> findByUserIdKeyword(String keyword) {
        List<User> result = new ArrayList<>();

        String sql = "SELECT Userid, Name, Password, Sex, Birth_date " +
                "FROM USERS " +
                "WHERE Userid LIKE '%' || ? || '%'";

        try {
            Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, keyword);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("Userid"),
                        rs.getString("Name")
                );
                result.add(user);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
