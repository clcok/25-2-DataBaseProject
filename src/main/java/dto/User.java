package main.java.dto;

public class User {

    private String userid;
    private String name;

    public User(String userid, String name) {
        this.userid = userid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUserid() {
        return userid;
    }

}
