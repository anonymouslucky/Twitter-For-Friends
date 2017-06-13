package com.lucky.friends;


import java.util.ArrayList;
import java.util.List;

public class User {

    private List<String> followers;
    private List<String> following;

    public User(){

    }

    public User(List<String> followers, List<String> following) {
        this.followers = followers;
        this.following = following;
    }

    @Override
    public String toString() {
        return "User{" +
                "followers=" + followers +
                ", following=" + following +
                '}';
    }

    public List<String> getFollowers() {
        if(followers == null){
            return new ArrayList<>();
        }
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        if(following == null){
            return new ArrayList<>();
        }
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }
}
