package edu.byu.cs.tweeter.server.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenChecker {
    private int timeLimit;
    public boolean isValid(AuthToken authToken){
        return Long.parseLong(authToken.datetime)+timeLimit > Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));
    }

    public AuthTokenChecker(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
