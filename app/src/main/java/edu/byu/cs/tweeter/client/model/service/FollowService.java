package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service{


    //public interface Observer<T> extends ServiceObserver {

      //  void addItems(List<T> items, boolean hasMorePages);

    //}

    public interface UserObserver extends ServiceObserver{
        void handleSuccess(User user);
    }

    public void loadMoreItems(User user, int pageSize, User lastFollowee, PagedPresenter<User>.PagedObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }
    public void getUserTask(String username, UserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                username, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);

    }

}
