package main.java;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import main.java.dao.*; 
import main.java.dto.*; 

public class App {

    // --- 전역 변수 ---
    private static User loggedInUser = null; 
    private Scanner scanner;
    
    // --- DAO 객체들 ---
    private UserDAO userDAO;
    private PostDAO postDAO;
    private FollowDAO followDAO;
    private LikeDAO likeDAO;
    private CommentDAO commentDAO;
    private BookmarkDAO bookmarkDAO;

    public App() {
        this.scanner = new Scanner(System.in);
        // DAO 초기화
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

    // ==========================================
    // 1. 비로그인 상태 메뉴
    // ==========================================
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
    
    // ==========================================
    // 2. 로그인 상태 메뉴
    // ==========================================
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

    // ==========================================
    // 3. 내 프로필 관련 기능
    // ==========================================
    private void handleMyProfile() {
        System.out.println("\n--- 내 프로필 ---");
        
        User profileUser = userDAO.getUserProfile(loggedInUser.getUserId());
        
        if (profileUser != null) {
            System.out.printf("<이름: %s, ID: %s, 좋아요 총 수: %d, 팔로워: %d, 팔로잉: %d>\n", 
                profileUser.getName(), 
                profileUser.getUserId(), 
                profileUser.getTotalLikes(), 
                profileUser.getFollowerCount(), 
                profileUser.getFollowingCount()
            );
        } else {
            System.out.println(">> 프로필 정보를 불러오는 데 실패했습니다.");
        }

        boolean inProfile = true;
        while (inProfile) {
            System.out.println("\n--- 내 프로필 메뉴 ---");
            System.out.println("1. 내 정보 수정");
            System.out.println("2. 내 북마크 조회");
            System.out.println("3. 게시물 올리기");
            System.out.println("4. 내 게시물 조회");
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴 선택: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1: handleEditMyInfo(); break; // [구현됨]
                case 2: handleViewMyBookmarks(); break;
                case 3: handleUploadPost(); break;
                case 4: handleViewMyPosts(); break;
                case 0: inProfile = false; break;
                default: System.out.println("잘못된 입력입니다.");
            }
        }
    }
    
    // [구현됨] 내 정보 수정 핸들러
    private void handleEditMyInfo() {
        System.out.println("\n--- 내 정보 수정하기 ---");
        System.out.println("1. 이름 수정하기");
        System.out.println("2. PW 수정하기");
        System.out.println("0. 취소");
        System.out.print("선택: ");

        int choice = getUserChoice();
        boolean success = false;

        switch(choice) {
            case 1: // 이름 변경
                System.out.print("새 이름을 입력하세요: ");
                String newName = scanner.nextLine();
                if (!newName.trim().isEmpty()) {
                    success = userDAO.updateUserName(loggedInUser.getUserId(), newName);
                    if (success) {
                        loggedInUser.setName(newName); // 현재 로그인 세션 정보 즉시 업데이트
                        System.out.println(">> 이름이 수정되었습니다.");
                    } else {
                        System.out.println(">> 수정 실패.");
                    }
                } else {
                    System.out.println(">> 이름을 입력해야 합니다.");
                }
                break;
            case 2: // 비번 변경
                System.out.print("새 PW를 입력하세요: ");
                String newPw = scanner.nextLine();
                if (!newPw.trim().isEmpty()) {
                    success = userDAO.updateUserPassword(loggedInUser.getUserId(), newPw);
                    if (success) {
                        loggedInUser.setPassword(newPw);
                        System.out.println(">> 비밀번호가 수정되었습니다.");
                    } else {
                        System.out.println(">> 수정 실패.");
                    }
                } else {
                     System.out.println(">> 비밀번호를 입력해야 합니다.");
                }
                break;
            case 0:
                System.out.println(">> 취소합니다.");
                break;
            default:
                System.out.println(">> 잘못된 입력입니다.");
        }
    }

    private void handleViewMyBookmarks() {
        System.out.println("\n--- 내 북마크 목록 ---");
        List<Post> bookmarks = bookmarkDAO.findBookmarkedPosts(loggedInUser.getUserId());
        
        if (bookmarks == null || bookmarks.isEmpty()) {
            System.out.println(">> 북마크한 게시물이 없습니다.");
        } else {
            showPostList(bookmarks);
        }
    }

    private void handleViewMyPosts() {
        System.out.println("\n--- 내가 쓴 게시물 목록 ---");
        List<Post> myPosts = postDAO.findPostsByUserId(loggedInUser.getUserId());
        
        if (myPosts == null || myPosts.isEmpty()) {
            System.out.println(">> 작성한 게시물이 없습니다.");
        } else {
            showPostList(myPosts);
        }
    }

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
    // 4. 게시물 조회 및 상세 보기 로직
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
                    case 0: 
                        inPostsMenu = false; 
                        break;
                    default: 
                        System.out.println("잘못된 입력입니다.");
                }
                
                if (posts != null) {
                    if (posts.isEmpty()) {
                        System.out.println(">> 표시할 게시물이 없습니다.");
                    } else {
                        showPostList(posts); 
                    }
                }
            } catch (Exception e) {
                System.out.println(">> [오류] 목록 조회 중 에러: " + e.getMessage());
            }
        }
    }

    // 게시물 목록 출력 헬퍼
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
    
    // 상세 보기 (이미지, 댓글, 좋아요, 북마크)
    private void handlePostDetail(int postId) {
        System.out.println("\n--- 게시물 상세 페이지 ---");
        
        Post post = postDAO.findPostById(postId);
        if (post == null) {
            System.out.println(">> 존재하지 않는 게시물입니다.");
            return;
        }

        boolean inPostDetail = true;
        while (inPostDetail) {
            // 화면 출력
            System.out.println("\n========================================");
            String imgStr = (post.getImageDir() == null) ? "(이미지 없음)" : "[첨부 이미지: " + post.getImageDir() + "]";
            System.out.printf("작성자: %s | 좋아요: %d\n", post.getUserId(), post.getLikeCount());
            System.out.println(imgStr);
            System.out.println("----------------------------------------");
            System.out.println(post.getContent());
            System.out.println("----------------------------------------");

            // 댓글 목록 출력
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
                System.out.println("  (댓글 조회 오류)");
            }
            System.out.println("========================================");

            // 상태 확인
            boolean isLiked = false;
            boolean isBookmarked = false;
            try {
                isLiked = likeDAO.checkLike(postId, loggedInUser.getUserId());
                isBookmarked = bookmarkDAO.checkBookmark(postId, loggedInUser.getUserId());
            } catch (Exception e) {}

            // 메뉴 출력
            if (isLiked) System.out.println("1. 좋아요 취소하기");
            else System.out.println("1. 좋아요 누르기");
            
            System.out.println("2. 댓글 작성하기");

            if (isBookmarked) System.out.println("3. 북마크 취소하기");
            else System.out.println("3. 북마크 추가하기");
            
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴 선택: ");
            
            int choice = getUserChoice();
            switch (choice) {
                case 1: // 좋아요
                    try {
                        boolean result = likeDAO.toggleLike(postId, loggedInUser.getUserId());
                        post = postDAO.findPostById(postId); // 정보 갱신
                        if (result) System.out.println(">> 좋아요를 눌렀습니다!");
                        else System.out.println(">> 좋아요를 취소했습니다.");
                    } catch (Exception e) { System.out.println(">> 좋아요 처리 실패"); }
                    break;
                case 2: // 댓글
                    handleWriteComment(post, comments);
                    break;
                case 3: // 북마크
                    try {
                        if (isBookmarked) {
                            bookmarkDAO.removeBookmark(postId, loggedInUser.getUserId());
                            System.out.println(">> 북마크를 취소했습니다.");
                        } else {
                            bookmarkDAO.addBookmark(postId, loggedInUser.getUserId());
                            System.out.println(">> 북마크에 추가했습니다.");
                        }
                    } catch (Exception e) { System.out.println(">> 북마크 처리 실패"); }
                    break;
                case 0: 
                    inPostDetail = false; 
                    break;
                default: 
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
    
    private void handleWriteComment(Post post, List<Comment> currentComments) {
        System.out.println("\n========== [댓글 작성] ==========");
        System.out.println("새로운 댓글을 쓰려면 '0'을 입력하고,");
        System.out.println("답글(대댓글)을 달려면 위 목록의 번호를 입력하세요.");
        System.out.print("입력 >> ");
        
        int selection = getUserChoice();
        
        if (selection < 0) {
            System.out.println(">> 잘못된 번호입니다.");
            return;
        }
        
        Integer parentId = null;
        
        // [수정됨] 선택한 댓글의 계층(깊이) 처리 로직 강화
        if (selection > 0) {
            if (currentComments == null || selection > currentComments.size()) {
                System.out.println(">> 목록에 없는 번호입니다.");
                return;
            }
            
            // 1. 사용자가 선택한 댓글 객체를 가져옵니다.
            Comment target = currentComments.get(selection - 1);
            
            // 2. 부모 ID 결정 로직 (계층 평탄화)
            // - 만약 선택한 댓글이 '원조 댓글(부모 없음)'이라면 -> 그 댓글이 내 부모가 됨.
            // - 만약 선택한 댓글이 '이미 답글(부모 있음)'이라면 -> 그 댓글의 부모가 내 부모가 됨 (형제로 들어감).
            if (target.getParentCommentId() == null || target.getParentCommentId() == 0) {
                parentId = target.getCommentId();
            } else {
                parentId = target.getParentCommentId();
            }
            
            System.out.println(">> [ " + target.getUserId() + " ] 님의 의견에 답글을 남깁니다.");
        }

        System.out.print("댓글 내용: ");
        String content = scanner.nextLine();
        
        if (content.trim().isEmpty()) {
            System.out.println(">> 내용을 입력해주세요.");
            return;
        }

        try {
            Comment newComment = new Comment(0, content, null, loggedInUser.getUserId(), post.getPostId(), parentId);
            boolean success = commentDAO.addComment(newComment);
            if (success) System.out.println(">> 댓글이 성공적으로 작성되었습니다!");
            else System.out.println(">> 댓글 작성에 실패했습니다.");
        } catch (Exception e) {
            System.out.println(">> [오류] 댓글 작성 중 에러 발생: " + e.getMessage());
        }
    }

    // ==========================================
    // 5. 사용자 검색 및 팔로우
    // ==========================================
    private void handleSearchUser() {
        System.out.println("\n--- 사용자 검색 ---");
        System.out.print("검색할 사용자 ID 키워드: ");
        String keyword = scanner.nextLine();

        if (keyword.trim().isEmpty()) {
            System.out.println(">> 검색어를 입력해주세요.");
            return;
        }

        List<User> results = userDAO.findByUserIdKeyword(keyword);

        if (results == null || results.isEmpty()) {
            System.out.println(">> '" + keyword + "'에 대한 검색 결과가 없습니다.");
            return;
        }

        System.out.println("\n[검색 결과]");
        for (int i = 0; i < results.size(); i++) {
            User u = results.get(i);
            System.out.printf("%d. %s (이름: %s)\n", (i + 1), u.getUserId(), u.getName());
        }

        System.out.print("\n프로필을 볼 사용자 번호를 입력하세요 (0은 취소): ");
        int selection = getUserChoice();

        if (selection > 0 && selection <= results.size()) {
            User targetUser = results.get(selection - 1);
            if (targetUser.getUserId().equals(loggedInUser.getUserId())) {
                System.out.println(">> 본인입니다. '내 프로필' 메뉴를 이용해주세요.");
            } else {
                handleOtherUserProfile(targetUser.getUserId());
            }
        }
    }

    private void handleOtherUserProfile(String targetUserId) {
        User profileUser = userDAO.getUserProfile(targetUserId);
        
        if (profileUser == null) {
            System.out.println(">> 사용자 정보를 불러오는 데 실패했습니다.");
            return;
        }

        boolean inProfile = true;
        while (inProfile) {
            System.out.println("\n========================================");
            System.out.printf("아이디: %s | 이름: %s\n", profileUser.getUserId(), profileUser.getName());
            System.out.printf("좋아요: %d | 팔로워: %d | 팔로잉: %d\n", 
                profileUser.getTotalLikes(), 
                profileUser.getFollowerCount(), 
                profileUser.getFollowingCount()
            );
            System.out.println("========================================");

            boolean isFollowing = false;
            try {
                isFollowing = followDAO.isIFollow(loggedInUser.getUserId(), targetUserId);
            } catch (Exception e) {}

            if (isFollowing) System.out.println("1. 언팔로우하기");
            else System.out.println("1. 팔로우하기");
            
            System.out.println("2. 작성한 게시물 보기");
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴 선택: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    if (isFollowing) {
                        followDAO.unfollow(loggedInUser.getUserId(), targetUserId);
                        System.out.println(">> 언팔로우했습니다.");
                    } else {
                        followDAO.follow(loggedInUser.getUserId(), targetUserId);
                        System.out.println(">> 팔로우했습니다.");
                    }
                    profileUser = userDAO.getUserProfile(targetUserId);
                    break;
                case 2:
                    handleViewUserPosts(targetUserId);
                    break;
                case 0:
                    inProfile = false;
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    // 타인이 작성한 게시물 목록 조회 헬퍼
    private void handleViewUserPosts(String targetUserId) {
        System.out.println("\n--- " + targetUserId + "님의 게시물 목록 ---");
        List<Post> posts = postDAO.findPostsByUserId(targetUserId);
        
        if (posts == null || posts.isEmpty()) {
            System.out.println(">> 작성한 게시물이 없습니다.");
        } else {
            showPostList(posts);
        }
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