package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticateView extends Presenter.View{
    public void displayInfoMessage(String message);
    public void handleSuccessful(User user, AuthToken authToken);
}
