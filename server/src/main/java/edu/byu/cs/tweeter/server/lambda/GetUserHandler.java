package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.Dynamo;
import edu.byu.cs.tweeter.server.service.UserService;

public class GetUserHandler implements RequestHandler<GetUserRequest, GetUserResponse> {
    @Override
    public GetUserResponse handleRequest(GetUserRequest input, Context context) {
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        UserService userService = new UserService(daoBuilder.getUserDAO());
        return userService.getUser(input);
    }
}
