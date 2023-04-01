package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse extends Response{
    public FollowResponse(String message) {
        super(true, message);
    }
    public FollowResponse(String message, Boolean success){
        super(success, message);
    }

}
