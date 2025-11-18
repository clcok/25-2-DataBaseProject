package main.java.service;

import java.util.Scanner;

public class login {

    Scanner sc = new Scanner(System.in);

    int input;

    public void loginMain(){
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
            default:
                System.out.println("잘못된 입력입니다.");
        }
    }

    public void loginSNS(){
        String id, pw;

        System.out.println("아이디를 입력하세요.");
        id = sc.nextLine();
        
        System.out.println("비밀번호를 입력하세요. ");
        pw = sc.nextLine();
        
        // DB와 연결
    }

    public void searchPassword(){

    }
}
