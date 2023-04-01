package edu.byu.cs.tweeter.client.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "follow";
    /**
     * The user that is being followed.
     */
    private final User followee;
    private final User currUser;

    public FollowTask(User currUser, AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.currUser = currUser;
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        try {
            FollowRequest request = new FollowRequest(followee, currUser, authToken);
            FollowResponse response = getServerFacade().follow(request, UserService.URL_PATH_FOLLOW);
            if (!response.isSuccess()) {
                sendFailedMessage(response.getMessage());
            }
            else{
                sendSuccessMessage();
            }
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

}
