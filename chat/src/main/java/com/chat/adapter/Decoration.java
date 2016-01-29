package com.chat.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ljy on 15/12/16.
 */
public class Decoration extends RecyclerView.ItemDecoration {
    private int span;

    public Decoration(int width) {
        span = width;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(span, span, span, span);
        if (parent.getChildAdapterPosition(view) < 3) {
            outRect.top = 0;
        }
    }
}
