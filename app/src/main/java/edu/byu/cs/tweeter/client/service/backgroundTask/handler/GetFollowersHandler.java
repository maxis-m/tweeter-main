package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersHandler extends BackgroundTaskHandler<PagedPresenter.PagedObserver> {

    public GetFollowersHandler(PagedPresenter.PagedObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedPresenter.PagedObserver observer, Bundle data) {
        List<User> followers = (List<User>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.addItems(followers, hasMorePages);
    }
}
