package com.example.eric.socials;

import java.util.ArrayList;

/**
 * Created by eric on 2/19/17.
 */

public class Social {
    String eventName;
    String eventImage;
    String emailOfCreator;
    String description;
    int numRSVP;
    String date;
    ArrayList<String> usersInterested;

    public Social(){}

    public Social(String eventName, String eventImage, String emailOfCreator, String description, int numRSVP, String date, ArrayList<String>usersInterested){
        this.eventName = eventName;
        this.eventImage = eventImage;
        this.emailOfCreator = emailOfCreator;
        this.description = description;
        this.numRSVP = numRSVP;
        this.date = date;
        this.usersInterested = usersInterested;
    }

    public String getEventName(){
        return eventName;
    }

    public String eventImage(){
        return eventImage;
    }

    public String getEmailOfCreator(){
        return emailOfCreator;
    }

    public String getDescription(){
        return description;
    }

    public int getNumRSVP(){
        return numRSVP;
    }

    public String getDate(){
        return date;
    }

    public ArrayList<String> getUsersInterested() {
        return usersInterested;
    }
}
