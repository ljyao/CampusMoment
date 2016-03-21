
package community.activity;

import android.os.Bundle;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.activities.BaseFragmentActivity;
import com.umeng.comm.ui.fragments.RelativeUserFragment;
import com.uy.bbs.R;

public class RelativeUserActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.umeng_comm_feeds_activity);
        int container = ResFinder.getId("umeng_comm_main_container");
        RelativeUserFragment relativeUserFragment = new RelativeUserFragment();
        relativeUserFragment.setArguments(getIntent().getExtras());
        setFragmentContainerId(container);
        showFragmentInContainer(container, relativeUserFragment);
    }
}
