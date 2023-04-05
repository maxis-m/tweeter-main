package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.beans.AuthTokens;
import edu.byu.cs.tweeter.server.dao.dynamo.beans.Users;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class UserDynamo implements UserDAO {
    private static final String TableName = "User";
    private static final String AuthTokenTableName = "AuthToken";

    private static final String UserAttr = "alias";
    private static final String AuthTokenAttr = "authToken";
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    public User getUser(GetUserRequest request) {
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(request.getAlias())
                .build();
        Users user = table.getItem(key);
        return new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImage());
    }

    public RegisterResponse register(RegisterRequest request) {
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(request.getUsername())
                .build();
        Users user = table.getItem(key);
        if(user == null){
            Users newUser = new Users();
            newUser.setAlias(request.getUsername());
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setPassword(request.getPassword());
            newUser.setFollower_count(0);
            newUser.setFollowing_count(0);
            AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion("us-east-2")
                    .build();

            byte[] byteArray = Base64.getDecoder().decode(request.getImage());

            ObjectMetadata data = new ObjectMetadata();

            data.setContentLength(byteArray.length);

            data.setContentType("image/jpeg");

            PutObjectRequest requestImage = new PutObjectRequest("tweeterimagesmaxwell", request.getUsername(), new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(requestImage);

            String link = "https://tweeterimagesmaxwell.s3.us-east-2.amazonaws.com/" + request.getUsername();
            newUser.setImage(link);
            table.putItem(newUser);
        }

        AuthToken authToken = new AuthToken();
        DynamoDbTable<AuthTokens> tableAuthToken = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class));
        Key keyAuthToken = Key.builder()
                .partitionValue(authToken.getToken())
                .build();
        AuthTokens authTokenItem = tableAuthToken.getItem(keyAuthToken);
        if(authTokenItem == null){
            AuthTokens newAuth = new AuthTokens();
            newAuth.setAuth_token(authToken.getToken());
            newAuth.setUser(request.getUsername());
            //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            newAuth.setTimestamp(authToken.getDatetime());
            tableAuthToken.putItem(newAuth);
        }
        User user2 = new User(request.getFirstName(), request.getLastName(), request.getUsername(), request.getImage());

        return new RegisterResponse(user2, authToken);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String alias = request.getUsername();
        String password = request.getPassword();
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();
        Users user = table.getItem(key);
        if(user == null){
            return new LoginResponse("No User with Username");
        }
        else if(!user.getPassword().equals(password)){
            return new LoginResponse("Incorrect Password");
        }
        else {
            AuthToken authToken = new AuthToken();
            DynamoDbTable<AuthTokens> tableAuthToken = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class));
            Key keyAuthToken = Key.builder()
                    .partitionValue(authToken.getToken())
                    .build();
            AuthTokens authTokenItem = tableAuthToken.getItem(keyAuthToken);
            if (authTokenItem == null) {
                AuthTokens newAuth = new AuthTokens();
                newAuth.setAuth_token(authToken.getToken());
                newAuth.setUser(request.getUsername());
                //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                newAuth.setTimestamp(authToken.getDatetime());
                tableAuthToken.putItem(newAuth);
            }
            User user2 = new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImage());
            return new LoginResponse(user2, authToken);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        DynamoDbTable<AuthTokens> table = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class));
        Key key = Key.builder()
                .partitionValue(request.getAuthToken().getToken())
                .build();
        if(table.getItem(key) != null){
            table.deleteItem(key);
        }
        return new LogoutResponse("Success");
    }

    @Override
    public void updateFollows(User follower, User followee, boolean isFollow) {
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key keyFollower = Key.builder()
                .partitionValue(follower.getAlias())
                .build();
        Key keyFollowee = Key.builder()
                .partitionValue(followee.getAlias())
                .build();
        Users followerUser = table.getItem(keyFollower);
        Users followeeUser = table.getItem(keyFollowee);
        if(isFollow) {
            followeeUser.setFollower_count(followeeUser.getFollower_count()+1);
            followerUser.setFollowing_count(followerUser.getFollowing_count()+1);
        }
        else{
            followeeUser.setFollower_count(followeeUser.getFollower_count()-1);
            followerUser.setFollowing_count(followerUser.getFollowing_count()-1);
        }
        table.updateItem(followeeUser);
        table.updateItem(followerUser);
    }

    @Override
    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(request.getTargetUser().getAlias())
                .build();
        Users user = table.getItem(key);
        if(user == null){
            return new GetFollowersCountResponse("No User Found");
        }
        else {
            return new GetFollowersCountResponse(user.getFollower_count());
        }
    }

    @Override
    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        DynamoDbTable<Users> table = enhancedClient.table(TableName, TableSchema.fromBean(Users.class));
        Key key = Key.builder()
                .partitionValue(request.getTargetUser().getAlias())
                .build();
        Users user = table.getItem(key);
        if(user == null){
            return new GetFollowingCountResponse("No User Found");
        }
        else {
            return new GetFollowingCountResponse(user.getFollowing_count());
        }
    }

    @Override
    public void deleteToken(String token) {
        DynamoDbTable<AuthTokens> tableAuthToken = enhancedClient.table(AuthTokenTableName, TableSchema.fromBean(AuthTokens.class));
        Key keyAuthToken = Key.builder()
                .partitionValue(token)
                .build();
        tableAuthToken.deleteItem(keyAuthToken);
    }
}
