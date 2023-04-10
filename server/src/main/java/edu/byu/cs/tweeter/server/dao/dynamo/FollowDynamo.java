package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.beans.Follows;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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
public class FollowDynamo implements FollowDAO {
    private static final String FollowsTableName = "follows";
    public static final String FollowsIndexName = "follows_index";

    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";
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
    public FollowResponse recordFollows(FollowRequest request) {
        //String followerHandle = request.get
        String follower_handle = request.getFollower().getAlias();
        String followee_handle = request.getFollowee().getAlias();
        DynamoDbTable<Follows> table = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        Follows follows = table.getItem(key);
        if(follows != null){

        }
        else {
            Follows newFollows = new Follows();
            newFollows.setFollower_handle(follower_handle);
            newFollows.setFollowee_handle(followee_handle);
            table.putItem(newFollows);
        }
        return new FollowResponse("Success");
    }
    public UnfollowResponse deleteFollows(UnfollowRequest request) {
        DynamoDbTable<Follows> table = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follows.class));

        Key key = Key.builder()
                .partitionValue(request.getFollower().getAlias()).sortValue(request.getFollowee().getAlias())
                .build();
        table.deleteItem(key);
        return new UnfollowResponse("Success");
    }



    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    @Override
    public Integer getFolloweeCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert follower != null;
        return 20;//getDummyFollowees().size();
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {
        DynamoDbTable<Follows> table = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(request.getFollowerAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());

        if(isNonEmptyString(request.getLastFolloweeAlias())) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(request.getFollowerAlias()).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(request.getLastFolloweeAlias()).build());
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest dbRequest = requestBuilder.build();

        DataPage<String> result = new DataPage<String>();

        PageIterable<Follows> pages = table.query(dbRequest);
        pages.stream()
                .limit(request.getLimit())
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows.getFollowee_handle()));
                });

        return new FollowingResponse(result.isHasMorePages(), result.getValues());
    }
    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        DynamoDbIndex<Follows> index = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follows.class)).index(FollowsIndexName);
        Key key = Key.builder()
                .partitionValue(request.getFolloweeAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());

        if(isNonEmptyString(request.getLastFollowerAlias())) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(request.getFolloweeAlias()).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(request.getLastFollowerAlias()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest dbRequest = requestBuilder.build();

        DataPage<String> result = new DataPage<String>();

        SdkIterable<Page<Follows>> sdkIterable = index.query(dbRequest);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(request.getLimit())
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows.getFollower_handle()));
                });


        return new FollowersResponse(result.isHasMorePages(), result.getValues());
    }
    @Override
    public FollowersResponse getAllFollowers(FollowersRequest request){
        DynamoDbIndex<Follows> index = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follows.class)).index(FollowsIndexName);
        Key key = Key.builder()
                .partitionValue(request.getFolloweeAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        QueryEnhancedRequest dbRequest = requestBuilder.build();

        DataPage<String> result = new DataPage<String>();

        SdkIterable<Page<Follows>> sdkIterable = index.query(dbRequest);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(follows -> result.getValues().add(follows.getFollower_handle()));
                });
        return new FollowersResponse(result.isHasMorePages(), result.getValues());
    }
    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        assert request.getFollower() != null;
        assert request.getFollowee() != null;
        DynamoDbTable<Follows> table = enhancedClient.table(FollowsTableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(request.getFollower().getAlias()).sortValue(request.getFollowee().getAlias())
                .build();
        Follows follows = table.getItem(key);
        return new IsFollowerResponse(follows != null);
    }

}
