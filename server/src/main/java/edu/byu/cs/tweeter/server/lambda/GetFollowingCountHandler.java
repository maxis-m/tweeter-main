package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.Dynamo;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<GetFollowingCountRequest, GetFollowingCountResponse> {
    @Override
    public GetFollowingCountResponse handleRequest(GetFollowingCountRequest input, Context context) {
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        FollowService followService = new FollowService(daoBuilder.getUserDAO(), daoBuilder.getFollowDAO());
        return followService.getFollowingCount(input);
    }
}
