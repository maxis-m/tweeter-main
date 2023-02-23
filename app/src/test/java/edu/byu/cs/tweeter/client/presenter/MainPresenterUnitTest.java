package edu.byu.cs.tweeter.client.presenter;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * When a user clicks the "POST STATUS" button, the View should call a "post status" operation
 * on a Presenter that posts the status to the server (although there isn't actually a server yet). Next, the Presenter's "post status" operation should:
 *
 * 1. Instruct the View to display a "Posting Status..." message.
 * 2. Create a Status object and call a Service to send it to the server.
 * 3. Instruct the View to display one of the following messages telling the user the outcome of the post operation:
 *      I. "Successfully Posted!"
 *      II. "Failed to post status: <ERROR MESSAGE>"
 *      III. "Failed to post status because of exception: <EXCEPTION MESSAGE>"
 *
 * Using JUnit and Mockito, write automated UNIT tests to verify the following:
 *
 * 1. The Presenter's "post status" operation works correctly in all three of the outcomes listed above
 * (succeeded, failed, failed due to exception).
 * 2. All parameters passed by the Presenter to the Service's "post status" operation are correct.
 *
 * This will require you to create mocks/spies for the View, Presenter, and Service.
 */


public class MainPresenterUnitTest {

    private MainPresenter.MainView mockView;
    private UserService mockUserService;
    private Cache mockCache;
    private MainPresenter mainPresenterSpy;
    private Status mockStatus;

    @BeforeEach
    public void setup(){
        //Create Mocks
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockUserService = Mockito.mock(UserService.class);
        mockCache = Mockito.mock(Cache.class);
        mockStatus = Mockito.mock(Status.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getUserService()).thenReturn(mockUserService);

        Cache.setInstance(mockCache);
    }

    @Test
    public void testPostStatus_statusSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserService.PostStatusObserver observer = invocation.getArgument(1, UserService.PostStatusObserver.class);
                observer.post();
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockUserService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus(mockStatus);
        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_statusFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserService.PostStatusObserver observer = invocation.getArgument(1, UserService.PostStatusObserver.class);
                observer.handleFailure("<ERROR MESSAGE>");
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockUserService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus(mockStatus);
        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockView).displayErrorMessage("Failed to post status: <ERROR MESSAGE>");
    }

    @Test
    public void testPostStatus_statusFailedWithException(){
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                UserService.PostStatusObserver observer = invocation.getArgument(1, UserService.PostStatusObserver.class);
                observer.handleException(new Exception("<EXCEPTION MESSAGE>"));
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockUserService).postStatus(Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus(mockStatus);
        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockView).displayErrorMessage("Failed to post status because of exception: <EXCEPTION MESSAGE>");
    }
}
