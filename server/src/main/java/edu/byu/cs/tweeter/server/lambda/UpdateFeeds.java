package edu.byu.cs.tweeter.server.lambda;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusFeedRequest;
import edu.byu.cs.tweeter.server.dao.DAOBuilder;
import edu.byu.cs.tweeter.server.dao.Dynamo;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;


public class UpdateFeeds implements RequestHandler<SQSEvent, Void>{
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Gson g = new Gson();
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        StatusService service = new StatusService(daoBuilder.getStatusDAO(), daoBuilder.getUserDAO(), daoBuilder.getFollowDAO());
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            PostStatusFeedRequest request = g.fromJson(msg.getBody(), PostStatusFeedRequest.class);
            service.addFeedBatch(request.getFollowers(), request.getStatus());
            //for(String alias : request.getFollowers()){
              //  service.postStatusFeed(alias, request.getStatus());
            //}
        }
        return null;
    }
}
