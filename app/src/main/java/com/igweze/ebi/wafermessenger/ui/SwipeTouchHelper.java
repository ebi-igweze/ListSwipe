package com.igweze.ebi.wafermessenger.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.igweze.ebi.wafermessenger.Functions.Function;


public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SwipeTouchHelperListener swipedListener;
    private Function<RecyclerView.ViewHolder, View> getForegroundView;

    public SwipeTouchHelper(int dragDirs, int swipeDirs, SwipeTouchHelperListener swipedListener, Function<RecyclerView.ViewHolder, View> getForegroundView) {
        super(dragDirs, swipeDirs);
        this.swipedListener = swipedListener;
        this.getForegroundView = getForegroundView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = getForegroundView.apply(viewHolder);
            // detect ui changes for only foreground view
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Row is swiped from recycler view
        swipedListener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getForegroundView.apply(viewHolder);
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getForegroundView.apply(viewHolder);
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = getForegroundView.apply(viewHolder);
        getDefaultUIUtil().clearView(foregroundView);
    }

    // swiped listener interface
    public interface SwipeTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}