package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.LoginHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService{
    public static final String URL_PATH_LOGIN = "/login";
    public static final String URL_PATH_REGISTER = "/register";
    public static final String URL_PATH_LOGOUT = "/logout";
    public static final String URL_PATH_FOLLOW = "/follow";
    public static final String URL_PATH_UNFOLLOW = "/unfollow";
    public static final String URL_PATH_GETUSER = "/getuser";

    public void login(String username, String password, LoginObserver observer){
        // Send the login request.
        LoginTask loginTask = new LoginTask(username, password, new LoginHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }


    public void register(String firstName, String lastName, String username, String password, ImageView imageToUpload, RegisterObserver observer){
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);
        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password, imageBytesBase64, new RegisterHandler(observer));

        BackgroundTaskUtils.runTask(registerTask);
    }

    public void isFollowerTask(User selectedUser, IsFollowingObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }
    public void unfollow(User selectedUser, UnFollowerObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler(observer));
        BackgroundTaskUtils.runTask(unfollowTask);
    }
    public void follow(User selectedUser, FollowerObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowHandler(observer));
        BackgroundTaskUtils.runTask(followTask);
    }
    public void logout(LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }
    public void postStatus(Status newStatus, PostStatusObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new PostStatusHandler(observer));
        BackgroundTaskUtils.runTask(statusTask);
    }
    public void getFollowerCount(User selectedUser, GetFollowersCountObserver observer) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowersCountHandler(observer));
        BackgroundTaskUtils.runTask(followersCountTask);
    }
    public void getFollowingCount(User selectedUser, GetFollowingCountObserver observer) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowingCountHandler(observer));
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    public interface LoginObserver extends ServiceObserver{
        void handleSuccess(User user, AuthToken authToken);
    }
    public interface RegisterObserver extends ServiceObserver{
        void handleSuccess(User user, AuthToken authToken);
    }
    public interface IsFollowingObserver extends ServiceObserver{
        void setFollow();

        void setNoFollow();

    }
    public interface UnFollowerObserver extends ServiceObserver{
        void updateFollow();

        
    }
    public interface FollowerObserver extends ServiceObserver {

        void updateFollow();
    }
    public interface LogoutObserver extends ServiceObserver{

        void logout();
    }
    public interface PostStatusObserver extends ServiceObserver{

        void post();

    }
    public interface GetFollowersCountObserver extends ServiceObserver{

        void getCount(Bundle data);

    }
    public interface GetFollowingCountObserver extends ServiceObserver{

        void getCount(Bundle data);

    }


}
