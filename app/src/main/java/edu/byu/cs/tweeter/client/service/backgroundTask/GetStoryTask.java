package edu.byu.cs.tweeter.client.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    private static final String LOG_TAG = "Story task";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        //return getFakeData().getPageOfStatus(getLastItem(), getLimit());

        try {
            User targetUserAlias = getTargetUser() == null ? null : getTargetUser();
            Status lastStatus = getLastItem() == null ? null : getLastItem();

            StoryRequest request = new StoryRequest(authToken, targetUserAlias, getLimit(), lastStatus);
            StoryResponse response = getServerFacade().getStory(request, StatusService.URL_PATH_GET_STORY);

            if (!response.isSuccess()) {
                sendFailedMessage(response.getMessage());
            }
            return new Pair<>(response.getFeed(), response.getHasMorePages());
            //sendSuccessMessage();
        } catch (IOException | TweeterRemoteException ex) {
            Log.e(LOG_TAG, "Failed to get story", ex);
            sendExceptionMessage(ex);
        }
        return null;
    }
}
