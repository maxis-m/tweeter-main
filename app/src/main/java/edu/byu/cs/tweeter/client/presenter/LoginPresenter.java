package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter<AuthenticateView> implements UserService.LoginObserver{

    public LoginPresenter(AuthenticateView view){
        super(view);
    }
    @Override
    public void handleSuccess(User user, AuthToken authToken) {
        view.handleSuccessful(user, authToken);
    }

    @Override
    public void handleFailure(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayInfoMessage(exception.getMessage());
    }



    public void initiateLogin(String username, String password){
        String validationMessage = validateLogin(username, password);

        if (validationMessage == null){
            view.displayInfoMessage("Logging in ....");
            UserService service = new UserService();
            service.login(username, password, this);
        }
        else{
            view.displayErrorMessage(validationMessage);
        }
    }

    public String validateLogin(String username, String password) {
        if (username.length() > 0 && username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }
}
