package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StatusDynamo implements StatusDAO{

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getTargetUser().getAlias() != null;

        List<Status> allFeed = getDummyFeed();
        List<Status> responseFeed = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFeed != null) {
                int feedIndex = getFeedStartingIndex(request.getLastStatus(), allFeed);

                for(int limitCounter = 0; feedIndex < allFeed.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                    responseFeed.add(allFeed.get(feedIndex));
                }

                hasMorePages = feedIndex < allFeed.size();
            }
        }

        return new FeedResponse(responseFeed, hasMorePages);
    }
    @Override
    public StoryResponse getStory(StoryRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getTargetUser().getAlias() != null;

        List<Status> allFeed = getDummyFeed();
        List<Status> responseFeed = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFeed != null) {
                int feedIndex = getFeedStartingIndex(request.getLastStatus(), allFeed);

                for(int limitCounter = 0; feedIndex < allFeed.size() && limitCounter < request.getLimit(); feedIndex++, limitCounter++) {
                    responseFeed.add(allFeed.get(feedIndex));
                }

                hasMorePages = feedIndex < allFeed.size();
            }
        }

        return new StoryResponse(responseFeed, hasMorePages);
    }
    @Override
    public PostStatusResponse postStatus(PostStatusRequest request) {
        assert request.getStatus() != null;
        return new PostStatusResponse();
    }


    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastStatus the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFeed the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFeedStartingIndex(Status lastStatus, List<Status> allFeed) {

        int feedIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFeed.size(); i++) {
                if(lastStatus.equals(allFeed.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    feedIndex = i + 1;
                    break;
                }
            }
        }

        return feedIndex;
    }



    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<Status> getDummyFeed() {
        return getFakeData().getFakeStatuses();
    }


    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }


}
