package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDynamo;

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
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new FollowingResponse("Auth Token expired");
        }
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        FollowingResponse response = getFollowingDAO().getFollowees(request);
        List<User> followees = new ArrayList<>();
        for(int i = 0; i < response.getFolloweesAlias().size(); i++){
            followees.add(getUserDAO().getUser(new GetUserRequest(response.getFolloweesAlias().get(i), new AuthToken())));
        }
        return new FollowingResponse(followees, response.getHasMorePages());
    }
    public FollowersResponse getFollowers(FollowersRequest request) {
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new FollowersResponse("Auth Token expired");
        }
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        FollowersResponse response = getFollowingDAO().getFollowers(request);
        List<User> followers = new ArrayList<>();
        for(int i = 0; i < response.getFollowersAlias().size(); i++){
            followers.add(getUserDAO().getUser(new GetUserRequest(response.getFollowersAlias().get(i), new AuthToken())));
        }
        return new FollowersResponse(followers, response.getHasMorePages());
    }
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new GetFollowersCountResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        return getUserDAO().getFollowersCount(request);
    }
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new GetFollowingCountResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        return getUserDAO().getFollowingCount(request);
    }
    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new IsFollowerResponse("Auth Token expired");
        }
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
    FollowDAO getFollowingDAO() {
        return new FollowDynamo();
    }
    UserDAO getUserDAO(){
        return new UserDynamo();
    }
    AuthTokenChecker getChecker(){
        return new AuthTokenChecker(30);
    }

    public FollowResponse follow(FollowRequest followRequest) {
        if(!getChecker().isValid(followRequest.getAuthToken())){
            getUserDAO().deleteToken(followRequest.getAuthToken().getToken());
            return new FollowResponse("Auth Token expired", false);
        }
        FollowResponse response = getFollowingDAO().recordFollows(followRequest);
        if(response.isSuccess()){
            getUserDAO().updateFollows(followRequest.getFollower(), followRequest.getFollowee(), true);
        }
        return response;
    }

    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) {
        if(!getChecker().isValid(unfollowRequest.getAuthToken())){
            getUserDAO().deleteToken(unfollowRequest.getAuthToken().getToken());
            return new UnfollowResponse("Auth Token expired", false);
        }
        return getFollowingDAO().deleteFollows(unfollowRequest);
    }
}
