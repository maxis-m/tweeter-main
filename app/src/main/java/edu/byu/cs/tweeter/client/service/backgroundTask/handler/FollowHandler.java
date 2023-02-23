package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class FollowHandler extends BackgroundTaskHandler<UserService.FollowerObserver> {

    public FollowHandler(UserService.FollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.FollowerObserver observer, Bundle data) {
        observer.updateFollow();
    }
}
