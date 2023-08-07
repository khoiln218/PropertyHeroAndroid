package com.gomicorp.propertyhero.callbacks;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by CTO-HELLOSOFT on 5/5/2016.
 */
public class RecyclerTouchListner implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private OnRecyclerTouchListener clickListener;

    public RecyclerTouchListner(Context context, final RecyclerView recyclerView, final OnRecyclerTouchListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null)
                    clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e))
            clickListener.onClick(child, rv.getChildLayoutPosition(child));

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
