package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowerPresenter extends PagedUserPresenter{

    public GetFollowerPresenter(FollowersView view){
        super(view);

    }

    public interface FollowersView extends PagedUserView{
        void addItems(List<User> followers);
    }


    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService.loadMoreItems(targetUser, pageSize, lastItem, new PagedObserver());
    }

}
