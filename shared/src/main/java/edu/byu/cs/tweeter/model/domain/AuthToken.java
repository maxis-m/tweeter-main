package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {
    /**
     * Value of the auth token.
     */
    public String token;
    /**
     * String representation of date/time at which the auth token was created.
     */
    public String datetime;

    public AuthToken() {
        UUID uuid = UUID.randomUUID();
        token = uuid.toString();
        datetime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    }

    public AuthToken(String token) {
        this.token = token;
    }

    public AuthToken(String token, String datetime) {
        this.token = token;
        this.datetime = datetime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDatetime() {
        return datetime;
    }
}