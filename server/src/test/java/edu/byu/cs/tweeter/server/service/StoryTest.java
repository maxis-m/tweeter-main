package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.StatusDynamo;

public class StoryTest {

    private StoryRequest request;
    private StoryResponse expectedResponse;
    private StatusDynamo mockStatusDAO;
    private StatusService statusServiceSpy;

    @BeforeEach
    public void setup() {
        User user = new User("FirstName", "LastName",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        AuthToken authToken = new AuthToken();
        Status resultStatus1 = new Status("Hello", user, (long)0, Arrays.asList("byu.edu"), Arrays.asList("@user2"));
        Status resultStatus2 = new Status("Hello2", user, (long)0, Arrays.asList("byu.edu"), Arrays.asList("@user3"));

        // Setup a request object to use in the tests
        request = new StoryRequest(authToken, user, 2, null);

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new StoryResponse(Arrays.asList(resultStatus1, resultStatus2), false);
        mockStatusDAO = Mockito.mock(StatusDynamo.class);
        Mockito.when(mockStatusDAO.getStory(request)).thenReturn(expectedResponse);

        statusServiceSpy = Mockito.spy(StatusService.class);
        Mockito.when(statusServiceSpy.getStatusDAO()).thenReturn(mockStatusDAO);
    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link FollowDynamo} class.
     */
    @Test
    public void testStory_validRequest_correctResponse() {
        StoryResponse response = statusServiceSpy.getStory(request);
        Assertions.assertEquals(expectedResponse, response);
    }
}
