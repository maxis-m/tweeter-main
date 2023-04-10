package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
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
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

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
    private DynamoDbClient client;
    private DynamoDbEnhancedClient enhancedClient;
    public DynamoDbClient getClient(){
        if(client == null){
            client = DynamoDbClient.builder()
                    .region(Region.US_EAST_2)
                    .build();
        }
        return client;
    }
    public DynamoDbEnhancedClient getEnhancedClient(){
        if(enhancedClient == null){
            enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(getClient())
                    .build();
        }
        return enhancedClient;
    }
    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        DynamoDbTable<Feed> table = getEnhancedClient().table(FeedTableName, TableSchema.fromBean(Feed.class));
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
        DynamoDbTable<Story> table = getEnhancedClient().table(StoryTableName, TableSchema.fromBean(Story.class));
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
    public PostStatusResponse postStatus(PostStatusRequest request) {
        assert request.getStatus() != null;
        DynamoDbTable<Story> table = getEnhancedClient().table(StoryTableName, TableSchema.fromBean(Story.class));
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
        return new PostStatusResponse();
    }
/**
    @Override
    public void postFeedStatus(String alias, Status status) {
        DynamoDbTable<Feed> feedTable = enhancedClient.table(FeedTableName, TableSchema.fromBean(Feed.class));
        Key followerKey = Key.builder()
                .partitionValue(alias).sortValue(status.getTimestamp())
                .build();
        Feed feed = feedTable.getItem(followerKey);
        if(feed == null) {
            Feed newFeed = new Feed();
            newFeed.setAlias(alias);
            newFeed.setAuthor(status.getUser().getAlias());
            newFeed.setTimestamp(status.getTimestamp());
            newFeed.setPost(status.getPost());
            newFeed.setMentions(status.getMentions());
            newFeed.setUrls(status.getUrls());
            newFeed.setFirstName(status.getUser().getFirstName());
            newFeed.setLastName(status.getUser().getLastName());
            newFeed.setImage(status.getUser().getImageUrl());
            feedTable.putItem(newFeed);
        }
    }**/

    @Override
    public void addFeedBatch(List<String> followers, Status status) {
        List<Feed> feed = new ArrayList<>();
        for(String follower : followers){
            Feed newFeed = new Feed();
            newFeed.setAlias(follower);
            newFeed.setAuthor(status.getUser().getAlias());
            newFeed.setTimestamp(status.getTimestamp());
            newFeed.setPost(status.getPost());
            newFeed.setMentions(status.getMentions());
            newFeed.setUrls(status.getUrls());
            newFeed.setFirstName(status.getUser().getFirstName());
            newFeed.setLastName(status.getUser().getLastName());
            newFeed.setImage(status.getUser().getImageUrl());
            feed.add(newFeed);
            if(feed.size() == 25){
                writeChunkOfFeed(feed);
                feed = new ArrayList<>();
            }
            //feedTable.putItem(newFeed);
        }
        if(feed.size() > 0){
            writeChunkOfFeed(feed);
        }
    }

    private void writeChunkOfFeed(List<Feed> feed){
        if(feed.size() > 25){
            throw new RuntimeException("Too many status' to write");
        }
        DynamoDbTable<Feed> table = getEnhancedClient().table(FeedTableName, TableSchema.fromBean(Feed.class));
        WriteBatch.Builder<Feed> writeBuilder = WriteBatch.builder(Feed.class).mappedTableResource(table);
        for (Feed item : feed) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = getEnhancedClient().batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFeed(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }



}
