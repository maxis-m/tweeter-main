package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.UserService;


public class IsFollowerHandler extends BackgroundTaskHandler<UserService.IsFollowingObserver> {
    public IsFollowerHandler(UserService.IsFollowingObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UserService.IsFollowingObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

        // If logged in user if a follower of the selected user, display the follow button as "following"
        if (isFollower) {
            observer.setFollow();

        } else {
            observer.setNoFollow();

        }
    }
}
