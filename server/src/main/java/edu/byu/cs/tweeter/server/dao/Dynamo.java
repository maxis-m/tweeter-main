package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import edu.byu.cs.tweeter.server.dao.dynamo.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDynamo;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDynamo;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Dynamo extends Db{

    @Override
    public UserDAO createUserDAO() {
        return new UserDynamo();
    }

    @Override
    public FollowDAO createFollowDAO() {
        return new FollowDynamo();
    }

    @Override
    public StatusDAO createStatusDAO() {
        return new StatusDynamo();
    }


}
