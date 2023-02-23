package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class UnfollowHandler extends BackgroundTaskHandler<UserService.UnFollowerObserver> {
    private UserService.UnFollowerObserver observer;
    public UnfollowHandler(UserService.UnFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.UnFollowerObserver observer, Bundle data) {
        observer.updateFollow();
    }
}
