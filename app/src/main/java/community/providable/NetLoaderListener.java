package community.providable;


/**
 * Created by ljy on 16/1/4.
 */
public abstract class NetLoaderListener<T> {
    public void onStart() {

    }

    public abstract void onComplete(boolean statue, T result);
}
