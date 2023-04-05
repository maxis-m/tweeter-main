package edu.byu.cs.tweeter.server.service;

import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDynamo;

public class UserService {
    private UserDAO userDAO;
    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }
    public GetUserResponse getUser(GetUserRequest request){
        if(request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        }
        if(!getChecker().isValid(request.getAuthToken())){
            userDAO.deleteToken(request.getAuthToken().getToken());
            return new GetUserResponse("Auth Token expired");
        }
        return new GetUserResponse(userDAO.getUser(request));
    }

    public LoginResponse login(LoginRequest request) throws NoSuchAlgorithmException {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        String newPassword = getPasswordService().getHashedPassword(request.getPassword(), request.getUsername());
        System.out.println(request.getUsername());
        System.out.println(newPassword);
        request.setPassword(newPassword);
        System.out.println(request.getPassword());
        return userDAO.login(request);
    }
    public LogoutResponse logout(LogoutRequest request){
        return userDAO.logout(request);
    }

    public RegisterResponse register(RegisterRequest request) throws NoSuchAlgorithmException {
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
        String newPassword = getPasswordService().getHashedPassword(request.getPassword(), request.getUsername());
        System.out.println(request.getUsername());
        System.out.println(newPassword);
        request.setPassword(newPassword);
        System.out.println(request.getPassword());
        return userDAO.register(request);
    }
    AuthTokenChecker getChecker(){
        return new AuthTokenChecker(60);
    }
    PasswordService getPasswordService(){
        return new PasswordService();
    }

}
