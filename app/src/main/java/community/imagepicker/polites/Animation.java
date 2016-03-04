package community.imagepicker.polites;

public interface Animation {

    /**
     * Transforms the view.
     *
     * @param view
     * @param time
     * @return true if this animation should remain active.  False otherwise.
     */
    boolean update(GestureImageView view, long time);

}
