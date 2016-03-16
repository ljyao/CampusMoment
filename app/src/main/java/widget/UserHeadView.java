package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Shine on 2016/3/12.
 */
@EViewGroup(R.layout.view_userhead)
public class UserHeadView extends RelativeLayout {
    public CommUser user;
    @ViewById(R.id.user_header)
    public SimpleDraweeView userHeader;

    public UserHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(CommUser user) {
        this.user = user;
    }
}
