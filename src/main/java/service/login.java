package main.java.service;

import java.util.Scanner;

public class login {

    static Scanner sc = new Scanner(System.in);

    static int input;

    static public void loginMain(){
        System.out.println("환영합니다. SNS 입니다.");

        System.out.println("1. 로그인하기");
        System.out.println("2. 비밀번호 찾기");

        System.out.print("이동하고 싶은 메뉴 번호를 입력해주세요: ");
        input = sc.nextInt();
        sc.nextLine();

        switch(input){
            case 1:
                loginSNS();
                break;
            case 2:
                searchPassword();
                break;
            default:
                System.out.println("잘못된 입력입니다.");
        }
    }

    static public void loginSNS(){
        String id, pw;

        System.out.print("아이디를 입력하세요: ");
        id = sc.nextLine();
        
        System.out.print("비밀번호를 입력하세요: ");
        pw = sc.nextLine();
        
        // DB와 연결
        // DB에서 찾고 없으면 0 반환 있으면 1 반환하도록?
    }

    static public void searchPassword(){
        String name, id, birthdate;
        char sex;

        System.out.print("이름을 입력하세요: ");
        name = sc.nextLine();
        System.out.print("아이디를 입력하세요: ");
        id =  sc.nextLine();
        System.out.print("성별을 입력하세요(M/F): ");
        sex = sc.next().charAt(0); // 문자열을 입력해도 하나만 받아들임
        sc.nextLine(); // \0을 제외하기 위해 nextLine() 호출
        
        System.out.print("생년월일을 입력하세요: ");
        birthdate = sc.nextLine();
        
        // DB연결 후 확인하는 로직 필요

    }
}
