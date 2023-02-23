package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedHandler extends BackgroundTaskHandler<PagedPresenter.PagedObserver> {

    public GetFeedHandler(PagedPresenter.PagedObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedPresenter.PagedObserver observer, Bundle data) {
        List<Status> statuses = (List<Status>) data.getSerializable(GetFeedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
        observer.addItems(statuses, hasMorePages);
    }
}