package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingHandler extends BackgroundTaskHandler<PagedPresenter.PagedObserver> {

    public GetFollowingHandler(PagedPresenter.PagedObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedPresenter.PagedObserver observer, Bundle data) {
        List<User> followees = (List<User>) data.getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.addItems(followees, hasMorePages);
    }
}
