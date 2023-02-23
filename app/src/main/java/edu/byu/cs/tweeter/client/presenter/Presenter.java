package edu.byu.cs.tweeter.client.presenter;

public abstract class Presenter<T extends Presenter.View> {
    public interface View {
        void displayErrorMessage(String message);
    }
    protected T view;
    protected Presenter(T view){
        this.view = view;
    }
}
