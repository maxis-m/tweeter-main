package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedUserPresenter extends PagedPresenter<User> {
    protected PagedUserPresenter(PagedUserView view) {
        super(view);
    }

    public interface PagedUserView extends PagedView<User>{

    }

}
