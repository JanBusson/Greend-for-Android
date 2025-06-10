package com.example.greendr;

//diese Java Klasse dient dazu die Snapshots aus Firebase später bei MyMatchesFragment anzuzeigen
public class Match {
    private String uid;
    private String name;
    private String imageUrl;

    // Leerer Konstruktor (Pflicht für Firebase)
    public Match() {}

    public Match(String uid, String name, String imageUrl) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getter und Setter
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

