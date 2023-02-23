package edu.byu.cs.tweeter.client.presenter;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter<MainPresenter.MainView>{




    public interface MainView extends View {

        void setFollow();

        void setNoFollow();

        void displayErrorMessage(String s);
        void displayInfoMessage(String s);

        void updateFollow(boolean b);

        void followButton(boolean b);

        void logout();

        void getFollowerCount(Bundle data);

        void getFollowingCount(Bundle data);
    }

    private UserService userService;
    public MainPresenter(MainView view){
        super(view);
        //userService = getUserService();
    }
    protected UserService getUserService(){
        if(userService == null){
            userService = new UserService();
        }
        return userService;
    }
    public void isFollower(User selectedUser) {
        getUserService().isFollowerTask(selectedUser, new IsFollowerObserver());
    }
    public void unfollow(User selectedUser) {
        getUserService().unfollow(selectedUser, new UnFollowerObserver());

    }
    public void follow(User selectedUser) {
        getUserService().follow(selectedUser, new FollowerObserver());
    }
    public void logout() {
        userService.logout(new LogoutObserver());
    }


    public void postStatus(Status newStatus) {
        view.displayInfoMessage("Posting Status...");
        getUserService().postStatus(newStatus, new PostStatusObserver());
    }


    public void getFollowerCount(User selectedUser) {
        getUserService().getFollowerCount(selectedUser, new GetFollowersCountObserver());
    }
    public void getFollowingCount(User selectedUser) {
        getUserService().getFollowingCount(selectedUser, new GetFollowingCountObserver());
    }
    private abstract class MainObserver implements ServiceObserver{
        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage(getMessage() + ": " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayErrorMessage(getMessage() + " because of exception: " + ex.getMessage());
        }
        public abstract String getMessage();
    }
    private class GetFollowingCountObserver extends MainObserver implements UserService.GetFollowingCountObserver{

        @Override
        public void getCount(Bundle data) {
            view.getFollowingCount(data);
        }

        @Override
        public String getMessage() {
            return "Failed to get Following Count";
        }
    }
    private class GetFollowersCountObserver extends MainObserver implements UserService.GetFollowersCountObserver{

        @Override
        public void getCount(Bundle data) {
            view.getFollowerCount(data);
        }

        @Override
        public String getMessage() {
            return "Failed to get Follower Count";
        }
    }
    private class PostStatusObserver extends MainObserver implements UserService.PostStatusObserver{

        @Override
        public void post() {
            view.displayInfoMessage("Successfully Posted!");
        }

        @Override
        public String getMessage() {
            return "Failed to post status";
        }
    }
    private class UnFollowerObserver extends MainObserver implements UserService.UnFollowerObserver{

        @Override
        public void updateFollow() {
            view.updateFollow(true);
            view.followButton(true);
        }
        @Override
        public String getMessage() {
            return "Failed to unfollow";
        }

    }
    private class FollowerObserver extends MainObserver implements UserService.FollowerObserver{

        @Override
        public void updateFollow() {
            view.updateFollow(false);
            view.followButton(true);
        }
        @Override
        public String getMessage() {
            return "Failed to follow";
        }
    }
    private class LogoutObserver extends MainObserver implements UserService.LogoutObserver{

        @Override
        public void logout() {
            view.logout();
        }

        @Override
        public String getMessage() {
            return "Failed to logout";
        }
    }
    private class IsFollowerObserver extends MainObserver implements UserService.IsFollowingObserver{

        @Override
        public void setFollow() {
            view.setFollow();

        }

        @Override
        public void setNoFollow() {
            view.setNoFollow();

        }
        @Override
        public String getMessage() {
            return "Failed to determine following relationship";
        }
    }
}
