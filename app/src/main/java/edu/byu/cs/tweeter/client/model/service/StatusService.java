package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.GetFeedHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service{



    //public interface Observer<T> extends ServiceObserver {

      //  void addItems(List<T> items, boolean hasMorePages);

    //}

    public void loadMoreItems(User user, int pageSize, Status lastStatus, PagedPresenter.PagedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new GetFeedHandler(observer));
        BackgroundTaskUtils.runTask(getFeedTask);
    }
    public void loadMoreStory(User user, int pageSize, Status lastStatus, PagedPresenter.PagedObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new GetFeedHandler(observer));
        BackgroundTaskUtils.runTask(getStoryTask);
    }


}
