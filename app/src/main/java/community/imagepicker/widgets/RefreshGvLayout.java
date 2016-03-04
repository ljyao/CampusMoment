
package community.imagepicker.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * GridView的下拉刷新布局组件
 */
public class RefreshGvLayout extends RefreshLayout<GridView> {

    public RefreshGvLayout(Context context) {
        this(context, null);
    }

    public RefreshGvLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
