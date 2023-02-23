package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginHandler extends BackgroundTaskHandler<UserService.LoginObserver>{

    UserService.LoginObserver observer;

    public LoginHandler(UserService.LoginObserver observer) {
        super(observer);
    }


    @Override
    protected void handleSuccessMessage(UserService.LoginObserver observer, Bundle data) {
        User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.handleSuccess(loggedInUser, authToken);

    }
}
