package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.Dynamo;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersCountHandler implements RequestHandler<GetFollowersCountRequest, GetFollowersCountResponse> {
    @Override
    public GetFollowersCountResponse handleRequest(GetFollowersCountRequest input, Context context) {
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        FollowService followService = new FollowService(daoBuilder.getUserDAO(), daoBuilder.getFollowDAO());
        return followService.getFollowersCount(input);
    }
}
