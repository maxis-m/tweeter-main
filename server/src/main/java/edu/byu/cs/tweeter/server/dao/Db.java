package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public abstract class Db {
    public abstract UserDAO createUserDAO();
    public abstract FollowDAO createFollowDAO();
    public abstract StatusDAO createStatusDAO();
}
