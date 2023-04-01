package edu.byu.cs.tweeter.client.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    private static final String LOG_TAG = "unfollow";
    /**
     * The user that is being followed.
     */
    private final User followee;
    private final User follower;

    public UnfollowTask(User follower, AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.follower = follower;
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        try {
            UnfollowRequest request = new UnfollowRequest(followee, follower, authToken);
            UnfollowResponse response = getServerFacade().unfollow(request, UserService.URL_PATH_UNFOLLOW);
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
