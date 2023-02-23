package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter extends PagedUserPresenter{
    public interface View extends PagedUserView{
        void addItems(List<User> followees);
    }

    public GetFollowingPresenter(View view){
        super(view);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService.loadMoreItems(targetUser, PAGE_SIZE, lastItem, new PagedObserver());
    }

}
