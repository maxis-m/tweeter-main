package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.PagedView>{
    public interface PagedView<S> extends View{
        void setLoading(boolean isLoading);
        public abstract void addItems(List<S> items);
        public abstract void navigateToUser(User user);
        void userMessage();
        void getUserSuccess(User user);
    }
    protected PagedPresenter(PagedView view){
        super(view);
        followService = new FollowService();
    }
    protected static final int PAGE_SIZE = 10;

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    protected User targetUser;
    protected AuthToken authToken;
    protected  T lastItem;

    public boolean hasMorePages() {
        return hasMorePages;
    }
    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }
    protected boolean hasMorePages = false;
    public boolean isLoading() {
        return isLoading;
    }
    public void setLoading(boolean loading) {
        isLoading = loading;
    }
    protected boolean isLoading = false;
    protected FollowService followService;


    public void getUser(String alias){
        followService.getUserTask(alias, new GetUserObserver());
        view.userMessage();
    }
    protected abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);
    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoading(true);
            getItems(authToken, targetUser, PAGE_SIZE, lastItem);
        }
    }

    private class GetUserObserver implements FollowService.UserObserver {

        @Override
        public void handleSuccess(User user) {
            view.getUserSuccess(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage(exception.getMessage());
        }
    }

    public class PagedObserver implements ServiceObserver{

        //@Override
        public void addItems(List<T> items, boolean hasMorePages) {
            setLoading(false);
            view.setLoading(false);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addItems(items);
        }

        @Override
        public void handleFailure(String s) {
            setLoading(false);
            view.setLoading(false);

            view.displayErrorMessage(s);
        }

        @Override
        public void handleException(Exception ex) {
            setLoading(false);
            view.setLoading(false);

            view.displayErrorMessage("Failed to get feed because of exception: " + ex.getMessage());
        }
    }
}
