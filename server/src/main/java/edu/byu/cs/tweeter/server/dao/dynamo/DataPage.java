package edu.byu.cs.tweeter.server.dao.dynamo;
import java.util.ArrayList;
import java.util.List;

/**
 * A page of data returned by the database.
 *
 * @param <T> type of data objects being returned.
 */
public class DataPage<T> {
    private List<T> values; // page of values returned by the database
    private boolean hasMorePages; // Indicates whether there are more pages of data available to be retrieved

    public DataPage() {
        setValues(new ArrayList<T>());
        setHasMorePages(false);
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public List<T> getValues() {
        return values;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }
    public void revlist(List<T> values)
    {
        // base condition when the list size is 0
        if (values.size() <= 1 || values == null)
            return;


        T value = values.remove(0);

        // call the recursive function to reverse
        // the list after removing the first element
        revlist(values);

        // now after the rest of the list has been
        // reversed by the upper recursive call,
        // add the first value at the end
        values.add(value);
    }
}

