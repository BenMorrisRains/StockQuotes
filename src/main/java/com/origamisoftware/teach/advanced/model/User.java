package com.origamisoftware.teach.advanced.model;

/**
 * A user for the stock ticker system.
 */
public class User {

    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
