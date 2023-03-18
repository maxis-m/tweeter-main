package edu.byu.cs.tweeter.model.net.response;



public class GetFollowersCountResponse extends Response{
    private int count;
    /**
     * Creates a response indicating that the corresponding request was unsuccessful. Sets the
     * success and more pages indicators to false.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public GetFollowersCountResponse(String message) {
        super(false, message);
    }


    public GetFollowersCountResponse(int count) {
        super(true, "Success");
        this.count = count;
    }

    public int getCount(){return count;}
}
