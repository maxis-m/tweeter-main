package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDynamo;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDynamo;

public class StatusService {
    private UserDAO userDAO;
    private FollowDAO followDAO;
    public StatusDAO statusDAO;

    public StatusService(StatusDAO statusDAO, UserDAO userDAO, FollowDAO followDAO) {
        this.statusDAO = statusDAO;
        this.userDAO = userDAO;
        this.followDAO = followDAO;
    }
    public FeedResponse getFeed(FeedRequest request) {
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new FeedResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return statusDAO.getFeed(request);
    }
    public StoryResponse getStory(StoryRequest request) {
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new StoryResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return statusDAO.getStory(request);
    }
    public PostStatusResponse postStatus(PostStatusRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new PostStatusResponse(false, "token timed out");
        }
        if(request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        FollowersRequest followersRequest = new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 0, null);
        FollowersResponse followersResponse = followDAO.getAllFollowers(followersRequest);

        return statusDAO.postStatus(request, followersResponse.getFollowersAlias());
    }

    AuthTokenChecker getChecker(){
        return new AuthTokenChecker(60);
    }


}

