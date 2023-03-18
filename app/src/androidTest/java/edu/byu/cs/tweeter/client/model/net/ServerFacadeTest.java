package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class ServerFacadeTest {
    private ServerFacade serverFacade;
    private RegisterRequest registerRequest;
    private RegisterRequest badRegisterRequest;
    private FollowersRequest followersRequest;
    private FollowersRequest badFollowersRequest;
    private GetFollowersCountRequest getFollowersCountRequest;
    private GetFollowersCountRequest badGetFollowersCountRequest;
    private RegisterResponse expectedRegisterResponse;
    private FollowersResponse followersResponse;
    private GetFollowersCountResponse getFollowersCountResponse;

    @BeforeEach
    public void setup(){
        serverFacade = new ServerFacade();
        String username = "@allen";
        String password = "password";
        String firstName = "Allen";
        String lastName = "Anderson";
        String image = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
        User user = new User("Allen", "Anderson", "@allen", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User user2 = new User("Amy", "Ames", "@amy", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        AuthToken authToken = new AuthToken();
        registerRequest = new RegisterRequest(username, password, firstName, lastName, image);
        badRegisterRequest = new RegisterRequest(username, password, firstName, lastName, null);
        followersRequest = new FollowersRequest(authToken, "@allen", 2, null);
        badFollowersRequest = new FollowersRequest(authToken, null, 2, null);
        getFollowersCountRequest = new GetFollowersCountRequest(authToken, user);
        badGetFollowersCountRequest = new GetFollowersCountRequest(authToken, null);
        expectedRegisterResponse = new RegisterResponse(user, authToken);
        followersResponse = new FollowersResponse(Arrays.asList(user, user2), true);
        getFollowersCountResponse = new GetFollowersCountResponse(20);
    }
    @Test
    public void testRegister_validRequest_correctResponse() {
        try {
            RegisterResponse response = serverFacade.register(registerRequest, "/register");
            Assertions.assertEquals(expectedRegisterResponse.getUser(), response.getUser());
            assert response.getAuthToken() != null;
        }
        catch(Exception ex){

        }
    }
    @Test
    public void testRegister_invalidRequest_correctResponse() {
        try {
            RegisterResponse response = serverFacade.register(badRegisterRequest, "/register");
            Assertions.assertTrue(!response.isSuccess());
        }
        catch(Exception ex){

        }
    }
    @Test
    public void testGetFollowers_validRequest_correctResponse() {
        try {
            FollowersResponse response = serverFacade.getFollowers(followersRequest, "/getfollowers");
            Assertions.assertEquals(followersResponse.getFollowers(), response.getFollowers());
        }
        catch(Exception ex){

        }
    }
    @Test
    public void testGetFollowers_invalidRequest_correctResponse() {
        try {
            FollowersResponse response = serverFacade.getFollowers(badFollowersRequest, "/getfollowers");
            Assertions.assertTrue(!response.isSuccess());
        }
        catch(Exception ex){

        }
    }
    @Test
    public void testGetFollowersCount_validRequest_correctResponse() {
        try {
            GetFollowersCountResponse response = serverFacade.getFollowersCount(getFollowersCountRequest, "/getfollowerscount");
            Assertions.assertEquals(getFollowersCountResponse.getCount(), response.getCount());
        }
        catch(Exception ex){

        }
    }
    @Test
    public void testGetFollowersCount_invalidRequest_correctResponse() {
        try {
            GetFollowersCountResponse response = serverFacade.getFollowersCount(badGetFollowersCountRequest, "/getfollowerscount");
            Assertions.assertTrue(!response.isSuccess());
        }
        catch(Exception ex){

        }
    }


}



