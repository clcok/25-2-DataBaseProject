package main.java.service;

import java.util.Scanner;

public class login {

    Scanner sc = new Scanner(System.in);

    int input;

    public void loginMain(){
        System.out.println("환영합니다. SNS 입니다.");

        System.out.println("1. 로그인하기");
        System.out.println("2. 비밀번호 찾기");

        input = sc.nextInt();

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

    }

    public void searchPassword(){

    }
}
