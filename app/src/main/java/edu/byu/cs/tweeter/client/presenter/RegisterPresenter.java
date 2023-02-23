package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter<AuthenticateView> implements UserService.RegisterObserver{

    public RegisterPresenter(AuthenticateView view){
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

    public void initiateRegister(String firstName, String lastName, String username, String password, ImageView imageToUpload){
        String validationMessage = validateRegistration(firstName, lastName, username, password, imageToUpload);
        if(validationMessage == null){
            view.displayInfoMessage("Registering....");
            UserService service = new UserService();
            service.register(firstName, lastName, username, password, imageToUpload, this);
        }
        else{
            view.displayErrorMessage(validationMessage);
        }
    }

    public String validateRegistration(String firstName, String lastName, String username, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (imageToUpload.getDrawable() == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }

}
