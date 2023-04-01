package edu.byu.cs.tweeter.model.net.request;


import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {


    private User followee;
    private User follower;
    private AuthToken authToken;

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getFollowee() {
        return followee;
    }
    public User getFollower(){ return follower; }

    public void setFollowee(User followee) {
        this.followee = followee;
    }
    public void setFollower(User follower){
        this.follower = follower;
    }



    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowRequest() {}

    public FollowRequest(User followee, User follower, AuthToken authToken) {
        this.followee = followee;
        this.follower = follower;
        this.authToken = authToken;
    }
}

