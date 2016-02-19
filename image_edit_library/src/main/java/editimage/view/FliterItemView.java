package editimage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uy.imageeditlibrary.R;

import adapter.ViewWrapper;
import editimage.fliter.FliterType;
import editimage.fragment.FliterListFragment;

/**
 * Created by shine on 16-2-19.
 */
public class FliterItemView extends RelativeLayout implements ViewWrapper.Binder<FliterType> {
    private TextView fliterTv;
    private ImageView fliterIv;
    private FliterType data;
    private FliterListFragment.FliterItemListener listener;

    public FliterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View itemView = inflate(getContext(), R.layout.fliter_itemview, this);
        fliterIv = (ImageView) itemView.findViewById(R.id.fliter_image);
        fliterTv = (TextView) itemView.findViewById(R.id.fliter_tv);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(data);
            }
        });
    }

    @Override
    public void bind(FliterType data) {
        this.data = data;
        fliterTv.setText(data.name);
    }

    public void setListener(FliterListFragment.FliterItemListener listener) {
        this.listener = listener;
    }
}
