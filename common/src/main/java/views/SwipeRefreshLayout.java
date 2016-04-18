package views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;


public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {
    // 刷新判断依赖的View
    private View mStartDependView;

    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStartDependView(View view) {
        mStartDependView = view;
    }

    public void setEntryAutoRefresh() {
        setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
    }

    @Override
    public boolean canChildScrollUp() {
        if (mStartDependView == null) {
            return super.canChildScrollUp();
        }

        View childView = mStartDependView;
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (childView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) childView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(childView, -1) || childView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(childView, -1);
        }
    }
}
