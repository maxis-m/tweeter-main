package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedStatusPresenter{




    public interface View extends PagedStatusView{
        void addItems(List<Status> statuses);
    }



    private StatusService statusService;


    public FeedPresenter(View view){
        super(view);
        statusService = new StatusService();
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService.loadMoreItems(targetUser, PAGE_SIZE, lastItem, new PagedObserver());
    }


}
