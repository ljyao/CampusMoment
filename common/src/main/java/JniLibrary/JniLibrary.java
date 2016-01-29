package JniLibrary;

/**
 * Created by ljy on 15/12/15.
 */
public class JniLibrary {
    static {
        System.loadLibrary("CommonLibrary");
    }

    public native void test(String input);

}
