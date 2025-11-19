package main.java;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import main.java.dao.*; 
import main.java.dto.*; 

public class App {

    private static User loggedInUser = null; 
    private Scanner scanner;
    
    private UserDAO userDAO;
    private PostDAO postDAO;
    private FollowDAO followDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;
    private BookmarkDAO bookmarkDAO;

    public App() {
        this.scanner = new Scanner(System.in);
        this.userDAO = new UserDAO();
        this.postDAO = new PostDAO();
        this.followDAO = new FollowDAO();
        this.likeDAO = new LikeDAO();
        this.commentDAO = new CommentDAO();
        this.bookmarkDAO = new BookmarkDAO();
    }

    public void run() {
        System.out.println("환영합니다. SNS입니다.");
        while (true) {
            if (loggedInUser == null) {
                showLoggedOutMenu();
            } else {
                showLoggedInMenu();
            }
        }
    }

    // --- 로그아웃 상태 메뉴 ---
    private void showLoggedOutMenu() {
        System.out.println("\n--- 메인 메뉴 ---");
        System.out.println("1. 로그인하기");
        System.out.println("2. 비밀번호찾기");
        System.out.println("0. 프로그램 종료");
        System.out.print("메뉴 선택: ");
        
        int choice = getUserChoice();
        switch (choice) {
            case 1: handleLogin(); break;
            case 2: handleFindPassword(); break;
            case 0: System.out.println("프로그램을 종료합니다."); System.exit(0);
            default: System.out.println("잘못된 입력입니다.");
        }
    }

    private void handleLogin() {
        System.out.println("\n--- 로그인 ---");
        System.out.print("a. 아이디를 입력하세요 : ");
        String id = scanner.nextLine();
        System.out.print("b. 비밀번호를 입력하세요 : ");
        String pw = scanner.nextLine();

        User user = userDAO.login(id, pw);
        if (user != null) {
            loggedInUser = user; 
            System.out.println(">> " + loggedInUser.getName() + "님, 환영합니다.");
        } else {
            System.out.println(">> 로그인 실패. 아이디 또는 비밀번호를 확인하세요.");
        }
    }

    private void handleFindPassword() {
        System.out.println("\n--- 비밀번호 찾기 ---");
        System.out.print("a. 이름을 입력하세요 : ");
        String name = scanner.nextLine();
        System.out.print("b. 아이디를 입력하세요 : ");
        String id = scanner.nextLine();
        System.out.print("c. 성별을 입력하세요(M/F) : ");
        String gender = scanner.nextLine();
        System.out.print("d. 생년월일을 입력하세요 (YYYYMMDD) : ");
        String birthdate = scanner.nextLine();

        String password = userDAO.findPassword(name, id, gender, birthdate);
        
        if (password != null) {
            System.out.println("회원님의 비밀번호는 [ " + password + " ] 입니다.");
        } else {
            System.out.println("없는 계정입니다. 입력 정보를 확인하세요.");
        }
    }
    
    // --- 로그인 상태 메뉴 ---
    private void showLoggedInMenu() {
        System.out.println("\n--- SNS 메뉴 (" + loggedInUser.getName() + " 님) ---");
        System.out.println("1. 내 프로필로 가기");
        System.out.println("2. 게시물 조회");
        System.out.println("3. 사용자 검색");
        System.out.println("0. 로그아웃");
        System.out.print("메뉴 선택: ");

        int choice = getUserChoice();
        switch (choice) {
            case 1: handleMyProfile(); break;
            case 2: handleViewPostsMenu(); break;
            case 3: handleSearchUser(); break;
            case 0: handleLogout(); break;
            default: System.out.println("잘못된 입력입니다.");
        }
    }
    
    private void handleLogout() {
        System.out.println(loggedInUser.getUserId() + "님이 로그아웃하셨습니다.");
        loggedInUser = null;
    }

    private void handleMyProfile() {
        System.out.println("--- 내 프로필 (TODO: 프로필 정보 표시) ---");
        boolean inProfile = true;
        while (inProfile) {
            System.out.println("\n--- 내 프로필 메뉴 ---");
            System.out.println("1. 내 정보 수정");
            System.out.println("2. 내 북마크 조회");
            System.out.println("3. 게시물 올리기");
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴 선택: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1: handleEditMyInfo(); break;
                case 2: handleViewMyBookmarks(); break;
                case 3: handleUploadPost(); break;
                case 0: inProfile = false; break;
                default: System.out.println("잘못된 입력입니다.");
            }
        }
    }
    
    private void handleEditMyInfo() { System.out.println("--- 내 정보 수정 (TODO) ---"); }
    private void handleViewMyBookmarks() { System.out.println("--- 내 북마크 (TODO) ---"); }

    private void handleUploadPost() {
        System.out.println("\n--- 게시물 올리기 ---");
        System.out.print("내용: ");
        String content = scanner.nextLine();
        System.out.print("이미지 경로(없으면 엔터): ");
        String imageDir = scanner.nextLine();
        System.out.print("해시태그(쉼표로 구분, 없으면 엔터): ");
        String tagsInput = scanner.nextLine();
        
        List<String> hashtags = new ArrayList<>();
        if (!tagsInput.trim().isEmpty()) {
            String[] tags = tagsInput.split(",");
            for (String t : tags) hashtags.add(t.trim());
        }
        
        boolean success = postDAO.uploadPost(loggedInUser.getUserId(), content, imageDir, hashtags);
        if (success) System.out.println(">> 게시물이 등록되었습니다!");
        else System.out.println(">> 게시물 등록 실패.");
    }

    // ==========================================
    // 게시물 조회 및 상세 보기 로직
    // ==========================================
    private void handleViewPostsMenu() {
        boolean inPostsMenu = true;
        while (inPostsMenu) {
            System.out.println("\n[게시물 조회]");
            System.out.println("1. 추천 게시물 조회");
            System.out.println("2. 팔로잉 게시물 조회");
            System.out.println("3. 해시태그로 검색하기");
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴 선택: ");

            int choice = getUserChoice();
            List<Post> posts = null;
            
            try {
                switch (choice) {
                    case 1: 
                        System.out.println("--- 추천 게시물 (가중치 기반) ---");
                        posts = postDAO.findRecommendedPosts(loggedInUser.getUserId());
                        break;
                    case 2:
                        System.out.println("--- 팔로잉 게시물 ---");
                        posts = postDAO.findFollowingPosts(loggedInUser.getUserId());
                        break;
                    case 3:
                        System.out.print("검색할 해시태그(# 제외): ");
                        String tag = scanner.nextLine();
                        posts = postDAO.findPostsByHashtag(tag);
                        break;
                    case 0: inPostsMenu = false; break;
                    default: System.out.println("잘못된 입력입니다.");
                }
                
                if (posts != null) {
                    if (posts.isEmpty()) System.out.println(">> 표시할 게시물이 없습니다.");
                    else showPostList(posts);
                }
            } catch (Exception e) {
                System.out.println(">> [오류] 목록 조회 중 에러: " + e.getMessage());
            }
        }
    }

    private void showPostList(List<Post> posts) {
        System.out.println("\n==================== 게시물 목록 ====================");
        System.out.println("번호\t| 작성자\t| 좋아요\t| 내용(요약)");
        System.out.println("-----------------------------------------------------");
        for (Post p : posts) {
            String summary = p.getContent();
            if (summary != null && summary.length() > 15) summary = summary.substring(0, 15) + "...";
            System.out.printf("%d\t| %s\t| %d\t| %s\n", p.getPostId(), p.getUserId(), p.getLikeCount(), summary);
        }
        System.out.println("=====================================================");
        System.out.print("상세히 볼 게시물 번호를 입력하세요 (0은 취소): ");
        int postId = getUserChoice();
        if (postId > 0) handlePostDetail(postId);
    }
    
    // --- 상세 보기 (좋아요 메뉴 동적 표시 & 댓글 작성) ---
    private void handlePostDetail(int postId) {
        System.out.println("\n--- 게시물 상세 페이지 ---");
        
        // 1. PostDAO를 통해 상세 정보 가져오기
        Post post = postDAO.findPostById(postId);
        if (post == null) {
            System.out.println(">> 존재하지 않는 게시물입니다.");
            return;
        }

        boolean inPostDetail = true;
        while (inPostDetail) {
            // 2. 화면 출력
            System.out.println("\n========================================");
            String imgStr = (post.getImageDir() == null) ? "(이미지 없음)" : "[첨부 이미지: " + post.getImageDir() + "]";
            System.out.printf("작성자: %s | 좋아요: %d\n", post.getUserId(), post.getLikeCount());
            System.out.println(imgStr);
            System.out.println("----------------------------------------");
            System.out.println(post.getContent());
            System.out.println("----------------------------------------");

            // 3. 댓글 목록 가져오기
            System.out.println("[댓글 목록]");
            List<Comment> comments = null;
            try {
                comments = commentDAO.findCommentsByPostId(postId);
                if (comments == null || comments.isEmpty()) {
                    System.out.println("  (작성된 댓글이 없습니다)");
                } else {
                    for (int i = 0; i < comments.size(); i++) {
                        Comment c = comments.get(i);
                        String prefix = (c.getParentCommentId() != null && c.getParentCommentId() != 0) ? "  └ " : "";
                        System.out.printf("%s%d. [%s] %s\n", prefix, (i+1), c.getUserId(), c.getContent());
                    }
                }
            } catch (Exception e) {
                System.out.println("  (댓글 오류)");
            }
            System.out.println("========================================");

            // 4. 하단 메뉴 (좋아요 상태에 따라 텍스트 변경)
            boolean isLiked = false;
            try {
                isLiked = likeDAO.checkLike(postId, loggedInUser.getUserId());
            } catch (Exception e) {
                // 에러 시 기본값 false
            }

            if (isLiked) {
                System.out.println("1. 좋아요 취소하기");
            } else {
                System.out.println("1. 좋아요 누르기");
            }
            System.out.println("2. 댓글 작성하기");
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴 선택: ");
            
            int choice = getUserChoice();
            switch (choice) {
                case 1: 
                    try {
                        boolean result = likeDAO.toggleLike(postId, loggedInUser.getUserId());
                        // 좋아요 상태 변경 후 게시물 정보 갱신 (좋아요 수 업데이트)
                        post = postDAO.findPostById(postId); 
                        if (result) System.out.println(">> 좋아요를 눌렀습니다!");
                        else System.out.println(">> 좋아요를 취소했습니다.");
                    } catch (Exception e) {
                        System.out.println(">> [오류] 좋아요 처리 실패");
                    }
                    break;
                case 2:
                    handleWriteComment(post, comments);
                    break;
                case 0: inPostDetail = false; break;
                default: System.out.println("잘못된 입력입니다.");
            }
        }
    }
    
    private void handleWriteComment(Post post, List<Comment> currentComments) {
        System.out.println("\n========== [댓글 작성] ==========");
        System.out.println("새로운 댓글을 쓰려면 '0'을 입력하고,");
        System.out.println("답글(대댓글)을 달려면 위 목록의 번호를 입력하세요.");
        System.out.print("입력 >> ");
        
        int selection = getUserChoice();
        
        // 1. 번호 유효성 검사
        if (selection < 0) {
            System.out.println(">> 잘못된 번호입니다.");
            return;
        }
        
        Integer parentId = null;
        
        // 2. 답글인 경우 (0번 아님) -> 실제 부모 댓글 ID 찾기
        if (selection > 0) {
            if (currentComments == null || selection > currentComments.size()) {
                System.out.println(">> 목록에 없는 번호입니다.");
                return;
            }
            // 화면에 보이는 순서(index) - 1 = 리스트 인덱스
            Comment target = currentComments.get(selection - 1);
            parentId = target.getCommentId(); // 실제 DB의 PK값 가져오기
            System.out.println(">> [ " + target.getUserId() + " ] 님의 댓글에 답글을 작성합니다.");
        }

        // 3. 내용 입력
        System.out.print("내용 입력: ");
        String content = scanner.nextLine();
        
        if (content.trim().isEmpty()) {
            System.out.println(">> 내용을 입력해주세요.");
            return;
        }

        try {
            // 4. DTO 생성 및 DAO 호출
            Comment newComment = new Comment(0, content, null, loggedInUser.getUserId(), post.getPostId(), parentId);
            boolean success = commentDAO.addComment(newComment);
            
            if (success) {
                System.out.println(">> 댓글이 성공적으로 작성되었습니다!");
            } else {
                System.out.println(">> 댓글 작성에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println(">> [오류] 댓글 작성 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSearchUser() {
        System.out.println("\n--- 사용자 검색 ---");
        System.out.print("검색할 사용자 ID: ");
        String targetUserId = scanner.nextLine();
        
        if (targetUserId.equals(loggedInUser.getUserId())) {
            System.out.println("본인입니다. '내 프로필' 메뉴를 이용해주세요.");
            return;
        }
        System.out.println("TODO: " + targetUserId + " 프로필 표시 (구현 예정)");
    }

    private int getUserChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            return choice;
        } catch (NumberFormatException e) {
            return -1; 
        }
    }
    
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}