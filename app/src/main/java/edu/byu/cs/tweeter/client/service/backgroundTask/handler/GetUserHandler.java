package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserHandler extends BackgroundTaskHandler<FollowService.UserObserver> {

    public GetUserHandler(FollowService.UserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowService.UserObserver observer, Bundle data) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.handleSuccess(user);
    }
}