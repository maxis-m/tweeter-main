package edu.byu.cs.tweeter.client.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends BackgroundTaskHandler<UserService.RegisterObserver> {


    public RegisterHandler(UserService.RegisterObserver observer) {
        super(observer);
    }


    @Override
    protected void handleSuccessMessage(UserService.RegisterObserver observer, Bundle data) {
        User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

        Cache.getInstance().setCurrUser(registeredUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.handleSuccess(registeredUser, authToken);
    }
}
