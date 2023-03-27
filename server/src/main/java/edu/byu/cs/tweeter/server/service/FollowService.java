package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDynamo;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDynamo} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowees(request);
    }
    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowers(request);
    }
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request){
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        return getFollowingDAO().getFollowersCount(request);
    }
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request){
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        return getFollowingDAO().getFollowingCount(request);
    }
    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(request.getFollower() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        else if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        return getFollowingDAO().isFollower(request);
    }

    /**
     * Returns an instance of {@link FollowDynamo}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDynamo getFollowingDAO() {
        return new FollowDynamo();
    }

    public FollowResponse follow(FollowRequest followRequest) {
        return new FollowResponse("Success");
    }

    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) {
        return new UnfollowResponse("Success");
    }
}
