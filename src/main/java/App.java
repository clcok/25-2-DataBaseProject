package main.java;

import main.java.dao.FollowDAO;
import main.java.dao.LikeDAO;
import main.java.dao.UserDAO;
import main.java.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class App {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final LikeDAO likeDAO = new LikeDAO();
    private static final FollowDAO followDAO = new FollowDAO();
    private static final LikeDAO likeDAO = new LikeDAO();
    private static final FollowDAO followDAO = new FollowDAO();

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

        System.out.print("조회할 사용자 번호를 입력하세요 (0: 뒤로가기): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice == 0) return;
        if (choice < 1 || choice > users.size()) {
            System.out.println("잘못된 번호입니다.");
            return;
        }

        // 선택된 사용자
        User target = users.get(choice - 1);
        showUserProfile(target);
    }

    private static void showUserProfile(User target) {
        String targetId = target.getUserid();

        // 1) 좋아요 / 팔로워 / 팔로잉 숫자 가져오기
        int totalLikes    = likeDAO.countLikesReceived(targetId);
        int followerCount = followDAO.countFollowers(targetId);
        int followingCount= followDAO.countFollowing(targetId);

        // 2) 기본 정보 + 통계 출력
        System.out.println("===== 사용자 프로필 =====");
        System.out.println("이름: " + target.getName());
        System.out.println("UserId: " + targetId);
        System.out.println("좋아요 총 수: " + totalLikes);
        System.out.println("팔로워: " + followerCount);
        System.out.println("팔로잉: " + followingCount);

        // 3) 자기 자신이면 팔로우 메뉴 안 띄우기
        if (targetId.equals(currentUserId)) {
            System.out.println("(내 계정이므로 팔로우 메뉴는 표시하지 않습니다.)");
            return;
        }

        // 4) 팔로우 상태 조회
        boolean iFollow     = followDAO.isIFollow(currentUserId, targetId);
        boolean heFollowsMe = followDAO.isHeFollowsMe(currentUserId, targetId);

        String actionLabel;
        if (!iFollow && !heFollowsMe) {
            actionLabel = "팔로우하기";
        } else if (iFollow && !heFollowsMe) {
            actionLabel = "언팔로우하기";
        } else if (!iFollow && heFollowsMe) {
            actionLabel = "맞팔로우하기";
        } else { // 둘 다 true
            actionLabel = "언팔로우하기 (맞팔 상태)";
        }

        System.out.println();
        System.out.println("1. " + actionLabel);
        System.out.println("2. 뒤로가기");
        System.out.print("선택: ");

        int menu = Integer.parseInt(sc.nextLine());

        if (menu == 1) {
            if (!iFollow && !heFollowsMe) {
                followDAO.follow(currentUserId, targetId);
                System.out.println("팔로우했습니다.");
            } else if (iFollow && !heFollowsMe) {
                followDAO.unfollow(currentUserId, targetId);
                System.out.println("언팔로우했습니다.");
            } else if (!iFollow && heFollowsMe) {
                followDAO.follow(currentUserId, targetId);
                System.out.println("맞팔로우가 되었습니다!");
            } else {
                followDAO.unfollow(currentUserId, targetId);
                System.out.println("언팔로우했습니다.");
            }
        }
    }
}
