package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDynamo implements UserDAO{

    public GetUserResponse getUser(GetUserRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.

        assert request.getAlias() != null;
        User user = getUser(request.getAlias());

        return new GetUserResponse(user);
    }
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    private User getUser(String alias) {
        return getFakeData().findUserByAlias(alias);
    }

    public RegisterResponse register(RegisterRequest request) {
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(user, authToken);
    }
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }
}
