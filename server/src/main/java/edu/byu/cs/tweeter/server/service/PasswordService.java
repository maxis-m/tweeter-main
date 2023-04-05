package edu.byu.cs.tweeter.server.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordService {

    public PasswordService(){}
    public String getHashedPassword(String password, String username) throws NoSuchAlgorithmException {
        //SecureRandom random = new SecureRandom();
        //byte[] salt = new byte[16];
        //random.nextBytes(salt);
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(username.getBytes());
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
