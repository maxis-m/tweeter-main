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


public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void>{
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Gson g = new Gson();
        DAOBuilder daoBuilder = new DAOBuilder(new Dynamo());
        FollowService service = new FollowService(daoBuilder.getUserDAO(), daoBuilder.getFollowDAO());
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            Status status = g.fromJson(msg.getBody(), Status.class);
            List<String> followers = service.getFollowersForStatus(status.getUser().getAlias(), 200, null);
            while(followers.size() > 0){
                PostStatusFeedRequest request = new PostStatusFeedRequest(followers, status);
                String messageBody = g.toJson(request);
                String queueUrl = "https://sqs.us-east-2.amazonaws.com/074642654070/UpdateFeedQueue";

                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(messageBody);

                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
                followers = service.getFollowersForStatus(status.getUser().getAlias(), 200, followers.get(followers.size()-1));
                if(followers == null){
                    break;
                }

            }

        }
        return null;
    }
}
