package com.example.eric.socials;

/**
 * Created by eric on 2/23/17.
 */

public class User {
    String name;
    String email;
    String profilePicture;

    public User() {}

    public User(String name, String profilePicture, String email){
        this.name = name;
        this.profilePicture = profilePicture;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
