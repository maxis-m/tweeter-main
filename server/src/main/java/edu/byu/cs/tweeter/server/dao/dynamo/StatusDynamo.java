package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.beans.Feed;
import edu.byu.cs.tweeter.server.dao.dynamo.beans.Story;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class StatusDynamo implements StatusDAO {
    private static final String StoryTableName = "Story";
    private static final String FeedTableName = "Feed";

    private static final String StoryAttr = "author_alias";
    private static final String FeedAttr = "alias";
    private static final String TimestampAttr = "timestamp";
    // DynamoDB client
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        DynamoDbTable<Feed> table = enhancedClient.table(FeedTableName, TableSchema.fromBean(Feed.class));
        Key key = Key.builder()
                .partitionValue(request.getTargetUser().getAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());

        if(request.getLastStatus() != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FeedAttr, AttributeValue.builder().s(request.getTargetUser().getAlias()).build());
            startKey.put(TimestampAttr, AttributeValue.builder().s(request.getLastStatus().getTimestamp().toString()).build());
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest dbRequest = requestBuilder.build();

        DataPage<Status> result = new DataPage<Status>();

        PageIterable<Feed> pages = table.query(dbRequest);
        pages.stream()
                .limit(request.getLimit())
                .forEach((Page<Feed> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(response -> result.getValues().add(new Status(response.getPost(),
                            new User( response.getFirstName(), response.getLastName(), response.getAuthor(),
                                    response.getImage()), response.getTimestamp(), response.getUrls(), response.getMentions())));
                });

        result.revlist(result.getValues());
        return new FeedResponse(result.getValues(), result.isHasMorePages());
    }
    @Override
    public StoryResponse getStory(StoryRequest request) {
        DynamoDbTable<Story> table = enhancedClient.table(StoryTableName, TableSchema.fromBean(Story.class));
        Key key = Key.builder()
                .partitionValue(request.getTargetUser().getAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());

        if(request.getLastStatus() != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(StoryAttr, AttributeValue.builder().s(request.getTargetUser().getAlias()).build());
            startKey.put(TimestampAttr, AttributeValue.builder().s(request.getLastStatus().getTimestamp().toString()).build());
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest dbRequest = requestBuilder.build();

        DataPage<Status> result = new DataPage<Status>();

        PageIterable<Story> pages = table.query(dbRequest);
        pages.stream()
                .limit(request.getLimit())
                .forEach((Page<Story> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(response -> result.getValues().add(new Status(response.getPost(),
                            new User( response.getFirstName(), response.getLastName(), response.getAuthor_alias(),
                                    response.getImage()), response.getTimestamp(), response.getUrls(), response.getMentions())));
                });

        result.revlist(result.getValues());
        return new StoryResponse(result.getValues(), result.isHasMorePages());
    }
    @Override
    public PostStatusResponse postStatus(PostStatusRequest request, List<String> followers) {
        assert request.getStatus() != null;
        DynamoDbTable<Story> table = enhancedClient.table(StoryTableName, TableSchema.fromBean(Story.class));
        Key key = Key.builder()
                .partitionValue(request.getStatus().getUser().getAlias()).sortValue(request.getStatus().getTimestamp())
                .build();

        Story story = table.getItem(key);
        if(story != null){
            return new PostStatusResponse(false, "status already exists");
        }
        else {
            Story newStory = new Story();
            //newStory.setStatus(request.getStatus());
            newStory.setAuthor_alias(request.getStatus().getUser().getAlias());
            newStory.setTimestamp(request.getStatus().getTimestamp());
            newStory.setPost(request.getStatus().getPost());
            newStory.setMentions(request.getStatus().getMentions());
            newStory.setUrls(request.getStatus().getUrls());
            newStory.setFirstName(request.getStatus().getUser().getFirstName());
            newStory.setLastName(request.getStatus().getUser().getLastName());
            newStory.setImage(request.getStatus().getUser().getImageUrl());
            table.putItem(newStory);
        }
        DynamoDbTable<Feed> feedTable = enhancedClient.table(FeedTableName, TableSchema.fromBean(Feed.class));
        for(int i=0; i< followers.size(); i++){
            Key followerKey = Key.builder()
                    .partitionValue(followers.get(i)).sortValue(request.getStatus().getTimestamp())
                    .build();
            Feed feed = feedTable.getItem(followerKey);
            if(feed != null){
                return new PostStatusResponse(false, "status already exists");
            }
            else{
                Feed newFeed = new Feed();
                newFeed.setAlias(followers.get(i));
                newFeed.setAuthor(request.getStatus().getUser().getAlias());
                newFeed.setTimestamp(request.getStatus().getTimestamp());
                newFeed.setPost(request.getStatus().getPost());
                newFeed.setMentions(request.getStatus().getMentions());
                newFeed.setUrls(request.getStatus().getUrls());
                newFeed.setFirstName(request.getStatus().getUser().getFirstName());
                newFeed.setLastName(request.getStatus().getUser().getLastName());
                newFeed.setImage(request.getStatus().getUser().getImageUrl());
                feedTable.putItem(newFeed);
            }
        }

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
