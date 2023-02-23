package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class GetFollowersCountHandler extends BackgroundTaskHandler<UserService.GetFollowersCountObserver> {
    public GetFollowersCountHandler(UserService.GetFollowersCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.GetFollowersCountObserver observer, Bundle data) {
        observer.getCount(data);
    }
}
