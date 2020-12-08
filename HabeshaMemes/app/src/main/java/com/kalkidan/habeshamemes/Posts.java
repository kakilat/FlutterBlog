package com.kalkidan.habeshamemes;

import java.util.HashMap;

public class Posts {

    public  String Uid, time, date, PostImage, Description, ProfileImage, FullName;

    public Posts() {
    }

    public Posts(String Uis, String time, String date, String postImage, String description, String profileImage, String fullName) {
 this.Uid=Uis;
 this.time=time;
 this.date=date;
 this.PostImage=postImage;
 this.Description=description;
 this.ProfileImage=profileImage;
 this.FullName=fullName;

    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }
}
