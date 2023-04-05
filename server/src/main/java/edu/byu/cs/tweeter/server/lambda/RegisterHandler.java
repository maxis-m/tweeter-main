package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.Dynamo;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, RegisterResponse> {
    @Override
    public RegisterResponse handleRequest(RegisterRequest registerRequest, Context context) {
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        UserService userService = new UserService(daoBuilder.getUserDAO());
        try {
            return userService.register(registerRequest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
