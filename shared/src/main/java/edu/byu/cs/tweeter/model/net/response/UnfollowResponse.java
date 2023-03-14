package edu.byu.cs.tweeter.model.net.response;

public class UnfollowResponse extends Response{
    public UnfollowResponse(String message) {
        super(true, message);
    }
}
