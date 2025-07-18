package com.example.greendr;

// Diese Klasse ist, damit beim Matching Nutzer aus dem Snapshot der Firebase DB automatisch auf die Attribute gemappt werden können

public class User {
    public String name;
    public String gender;
    public String homeTown;
    public String jobTitle;
    public String university;
    public String languages;
    public String sexuality;
    public String workplace;
    public String bio;

    public User() {} // Firebase braucht einen leeren Konstruktor

    public User(String name, String gender, String homeTown, String jobTitle,
                String university, String languages, String sexuality,
                String workplace, String bio) {
        this.name = name;
        this.gender = gender;
        this.homeTown = homeTown;
        this.jobTitle = jobTitle;
        this.university = university;
        this.languages = languages;
        this.sexuality = sexuality;
        this.workplace = workplace;
        this.bio = bio;
    }
}
