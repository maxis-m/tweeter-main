package edu.byu.cs.tweeter.server.dao.dynamo.beans;

import edu.byu.cs.tweeter.server.dao.dynamo.FollowDynamo;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class Follows {
    private String follower_handle;
    private String followee_handle;
    private String follower_name;
    private String followee_name;

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    public String getFollowee_name() {
        return followee_name;
    }

    public void setFollowee_name(String followee_name) {
        this.followee_name = followee_name;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowDynamo.FollowsIndexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower) {
        this.follower_handle = follower;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowDynamo.FollowsIndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee) {
        this.followee_handle = followee;
    }



    @Override
    public String toString() {
        return "Follows{" +
                "follower_handle='" + follower_handle + '\'' +
                ", followee_handle='" + followee_handle + '\'' +
                ", follower_name='" + follower_name + '\'' +
                ", followee_name='" + followee_name + '\'' +
                '}';
    }
}