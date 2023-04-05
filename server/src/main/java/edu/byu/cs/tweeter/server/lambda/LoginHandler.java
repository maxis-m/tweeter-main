package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.Dynamo;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class LoginHandler implements RequestHandler<LoginRequest, LoginResponse> {
    @Override
    public LoginResponse handleRequest(LoginRequest loginRequest, Context context) {
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        UserService userService = new UserService(daoBuilder.getUserDAO());
        try {
            return userService.login(loginRequest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
