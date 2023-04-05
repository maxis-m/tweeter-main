package edu.byu.cs.tweeter.server.dao;

public class DAOBuilder {
    private UserDAO userDAO;
    private FollowDAO followDAO;
    private StatusDAO statusDAO;
    public DAOBuilder(Db database){
        userDAO = database.createUserDAO();
        followDAO = database.createFollowDAO();
        statusDAO = database.createStatusDAO();
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public FollowDAO getFollowDAO() {
        return followDAO;
    }

    public StatusDAO getStatusDAO() {
        return statusDAO;
    }
}
