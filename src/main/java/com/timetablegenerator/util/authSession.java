package com.timetablegenerator.util;

import com.timetablegenerator.model.userModel;

public class authSession {
    private static userModel loggedInUser;

    public static void setUser(userModel user) {
        loggedInUser = user;
    }

    public static userModel getUser() {
        return loggedInUser;
    }

    public static void clear() {
        loggedInUser = null;
    }
}
