package edu.byu.cs.tweeter.model.net.request;


import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {


    private User followee;
    public User getFollowee() {
        return followee;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }



    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private FollowRequest() {}

    public FollowRequest(User followee) {
        this.followee = followee;
    }
}

