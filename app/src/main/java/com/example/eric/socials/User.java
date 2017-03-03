package com.example.eric.socials;

import java.util.ArrayList;

/**
 * Created by eric on 2/23/17.
 */

public class User {
    String name;
    String email;
    String profilePicture;

    public User(String name, String profilePicture, String email){
        this.name = name;
        this.profilePicture = profilePicture;
        this.email = email;
    }
}
