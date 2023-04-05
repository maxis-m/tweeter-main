package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dynamo.FollowDynamo;
import edu.byu.cs.tweeter.server.dao.dynamo.StatusDynamo;
import edu.byu.cs.tweeter.server.dao.dynamo.UserDynamo;

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
