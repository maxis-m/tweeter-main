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
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDynamo;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    private UserDAO userDAO;
    public FollowDAO followDAO;

    public FollowService(UserDAO userDAO, FollowDAO followDAO) {
        this.userDAO = userDAO;
        this.followDAO = followDAO;
    }

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
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new FollowingResponse("Auth Token expired");
        }
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        FollowingResponse response = followDAO.getFollowees(request);
        List<User> followees = new ArrayList<>();
        for(int i = 0; i < response.getFolloweesAlias().size(); i++){
            followees.add(userDAO.getUser(new GetUserRequest(response.getFolloweesAlias().get(i), new AuthToken())));
        }
        return new FollowingResponse(followees, response.getHasMorePages());
    }
    public FollowersResponse getFollowers(FollowersRequest request) {
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new FollowersResponse("Auth Token expired");
        }
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        FollowersResponse response = followDAO.getFollowers(request);
        List<User> followers = new ArrayList<>();
        for(int i = 0; i < response.getFollowersAlias().size(); i++){
            followers.add(userDAO.getUser(new GetUserRequest(response.getFollowersAlias().get(i), new AuthToken())));
        }
        return new FollowersResponse(followers, response.getHasMorePages());
    }
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new GetFollowersCountResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        return userDAO.getFollowersCount(request);
    }
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new GetFollowingCountResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        return userDAO.getFollowingCount(request);
    }
    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new IsFollowerResponse("Auth Token expired");
        }
        if(request.getFollower() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        }
        else if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        }
        return followDAO.isFollower(request);
    }



    AuthTokenChecker getChecker(){
        return new AuthTokenChecker(60);
    }

    public FollowResponse follow(FollowRequest followRequest) {
        if(!getChecker().isValid(followRequest.getAuthToken())){
            userDAO.deleteToken(followRequest.getAuthToken().getToken());
            return new FollowResponse("Auth Token expired", false);
        }
        FollowResponse response = followDAO.recordFollows(followRequest);
        if(response.isSuccess()){
            userDAO.updateFollows(followRequest.getFollower(), followRequest.getFollowee(), true);
        }
        return response;
    }

    public UnfollowResponse unfollow(UnfollowRequest unfollowRequest) {
        if(!getChecker().isValid(unfollowRequest.getAuthToken())){
            userDAO.deleteToken(unfollowRequest.getAuthToken().getToken());
            return new UnfollowResponse("Auth Token expired", false);
        }
        //Update count
        return followDAO.deleteFollows(unfollowRequest);
    }
}
