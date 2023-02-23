package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class GetFollowingCountHandler extends BackgroundTaskHandler<UserService.GetFollowingCountObserver> {

    private UserService.GetFollowingCountObserver observer;
    public GetFollowingCountHandler(UserService.GetFollowingCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.GetFollowingCountObserver observer, Bundle data) {
        observer.getCount(data);
    }
}