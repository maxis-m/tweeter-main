package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.Status;

public abstract class PagedStatusPresenter extends PagedPresenter<Status>{


    public interface PagedStatusView extends PagedView<Status> {
    }
    protected PagedStatusPresenter(PagedStatusView view) {
        super(view);
    }
}
