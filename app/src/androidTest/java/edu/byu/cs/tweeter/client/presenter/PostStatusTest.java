package edu.byu.cs.tweeter.client.presenter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class PostStatusTest {

    private MainPresenter.MainView mockView;
    private MainPresenter presenterSpy;
    private ServerFacade serverFacade;
    private CountDownLatch countDownLatch;
    private String alias = "@test";
    private String password = "p";
    private String post = "Testing 1";
    //private Cache cacheSpy;

    @BeforeEach
    public void setup(){
        serverFacade = new ServerFacade();
        mockView = Mockito.mock(MainPresenter.MainView.class);
        presenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(presenterSpy.getPostStatusObserver()).thenReturn(new PostStatusTestObserver());
        //cacheSpy = Mockito.spy(Cache.class);
        resetCountDown();
    }
    private void resetCountDown(){
        countDownLatch = new CountDownLatch(1);
    }
    private void awaitCountDown() throws InterruptedException{
        countDownLatch.await();
        resetCountDown();
    }
    private class PostStatusTestObserver implements UserService.PostStatusObserver {

        @Override
        public void post() {
            mockView.displayInfoMessage("Successfully Posted!");
            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            mockView.displayErrorMessage("Failed to post status: " + message);
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            mockView.displayErrorMessage("Failed to post status because of exception: " + exception.getMessage());
            countDownLatch.countDown();
        }
    }

    @Test
    public void postStatusTest_success() throws InterruptedException{
        LoginResponse response = null;
        try{
            response = serverFacade.login(new LoginRequest(alias, password), UserService.URL_PATH_LOGIN);
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        Status status = new Status(post, response.getUser(), (long)0, null, null);
        //Mockito.when(cacheSpy.getInstance().getCurrUserAuthToken()).thenReturn(response.getAuthToken());
        Cache.getInstance().setCurrUserAuthToken(response.getAuthToken());
        presenterSpy.postStatus(status);
        awaitCountDown();

        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");
        StoryResponse storyResponse = null;
        try{
            storyResponse = serverFacade.getStory(new StoryRequest(response.getAuthToken(), response.getUser(), 1, null), StatusService.URL_PATH_GET_STORY);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        assertTrue(storyResponse.getFeed().contains(status));
    }


}



