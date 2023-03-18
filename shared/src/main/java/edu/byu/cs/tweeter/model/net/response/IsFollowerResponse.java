package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerResponse extends Response{
    private Boolean isFollower;


    public Boolean getIsFollower() {
        return isFollower;
    }
    public IsFollowerResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param isFollower if the user is a follower or not
     */
    public IsFollowerResponse(Boolean isFollower) {
        super(true, null);
        this.isFollower = isFollower;
    }
}
