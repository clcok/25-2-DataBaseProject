package main.java;

import main.java.dao.UserDAO;
import main.java.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class App {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {

        searchUserMenu();

    }

    public static void searchUserMenu() {

        System.out.println("[사용자 검색]");
        System.out.println("1. 아이디로 검색");
        System.out.println("2. 이름으로 검색");
        System.out.println("0. 뒤로가기");
        System.out.print("선택: ");

        int menu = Integer.parseInt(sc.nextLine());

        if (menu == 0) return;

        System.out.print("검색할 아이디 일부를 입력하세요: ");
        String keyword = sc.nextLine();

        List<User> users = new ArrayList<>();

        try {
            if (menu == 1) {
                users = userDAO.findByUserIdKeyword(keyword);
            } else if (menu == 2) {
                users = userDAO.findByNameKeyword(keyword);
            } else {
                System.out.println("잘못된 입력입니다.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (users.isEmpty()) {
            System.out.println("검색 결과 없음!");
            return;
        }

        System.out.println("----- 검색 결과 -----");
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("%d. %s (%s)\n",
                    i + 1,
                    users.get(i).getName(),
                    users.get(i).getUserid());
        }
    }
}
