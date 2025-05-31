package com.example.greendr;

public class UserProfile {
    public String name;
    public int age;
    public String interests;

    // Leerer Konstruktor (wichtig für Firebase!)
    public UserProfile() {}

    public UserProfile(String name, int age, String interests) {
        this.name = name;
        this.age = age;
        this.interests = interests;
    }
}
