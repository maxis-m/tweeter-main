package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StatusDynamo;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDynamo;

public class StatusService {
    public FeedResponse getFeed(FeedRequest request) {
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new FeedResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStatusDAO().getFeed(request);
    }
    public StoryResponse getStory(StoryRequest request) {
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new StoryResponse("Auth Token expired");
        }
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStatusDAO().getStory(request);
    }
    public PostStatusResponse postStatus(PostStatusRequest request){
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new PostStatusResponse(false);
        }
        if(request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        FollowersRequest followersRequest = new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 0, null);
        FollowersResponse followersResponse = getFollowDAO().getAllFollowers(followersRequest);

        return getStatusDAO().postStatus(request, followersResponse.getFollowersAlias());
    }

    /**
     * Returns an instance of {@link StatusDynamo}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    StatusDAO getStatusDAO() {
        return new StatusDynamo();
    }
    FollowDAO getFollowDAO() {
        return new FollowDynamo();
    }
    UserDAO getUserDAO(){
        return new UserDynamo();
    }
    AuthTokenChecker getChecker(){
        return new AuthTokenChecker(30);
    }


}

