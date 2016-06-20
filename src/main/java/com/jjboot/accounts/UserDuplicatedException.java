package com.jjboot.accounts;

/**
 * Created by jin on 2016-06-19.
 */
public class UserDuplicatedException extends RuntimeException {

    String username;

    public UserDuplicatedException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
