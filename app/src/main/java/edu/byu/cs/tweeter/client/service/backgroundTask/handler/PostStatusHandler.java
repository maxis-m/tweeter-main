package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class PostStatusHandler extends BackgroundTaskHandler<UserService.PostStatusObserver> {
    public PostStatusHandler(UserService.PostStatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.PostStatusObserver observer, Bundle data) {
        observer.post();
    }

}
