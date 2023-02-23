package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class LogoutHandler extends BackgroundTaskHandler<UserService.LogoutObserver> {
    private UserService.LogoutObserver observer;
    public LogoutHandler(UserService.LogoutObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.LogoutObserver observer, Bundle data) {
        observer.logout();
    }


}
