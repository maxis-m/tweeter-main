package edu.byu.cs.tweeter.server.service;

import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDynamo;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {
    public GetUserResponse getUser(GetUserRequest request){
        if(request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        }
        if(!getChecker().isValid(request.getAuthToken())){
            getUserDAO().deleteToken(request.getAuthToken().getToken());
            return new GetUserResponse("Auth Token expired");
        }
        return new GetUserResponse(getUserDAO().getUser(request));
    }

    public LoginResponse login(LoginRequest request) throws NoSuchAlgorithmException {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        getPasswordService().setPassword(request.getPassword());
        request.setPassword(getPasswordService().getHashedPassword());
        return getUserDAO().login(request);
    }
    public LogoutResponse logout(LogoutRequest request){
        return getUserDAO().logout(request);
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a First Name");
        }
        else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a Last Name");
        }
        else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }

        return getUserDAO().register(request);
    }
    UserDAO getUserDAO(){
        return new UserDynamo();
    }
    AuthTokenChecker getChecker(){
        return new AuthTokenChecker(30);
    }
    PasswordService getPasswordService(){
        return new PasswordService();
    }

}
