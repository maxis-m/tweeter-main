package edu.byu.cs.tweeter.client.service.observer;

import android.view.View;

import edu.byu.cs.tweeter.client.presenter.Presenter;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception exception);

}
