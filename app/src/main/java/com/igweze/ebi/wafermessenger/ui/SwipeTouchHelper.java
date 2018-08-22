package com.igweze.ebi.wafermessenger.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.igweze.ebi.wafermessenger.Functions.Function;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;


enum ButtonState {
    GONE,
    VISIBLE
}

enum TouchType {
    ALL,
    UP,
    DOWN,
    NONE
}

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SwipeTouchHelperListener swipedListener;
    private Function<RecyclerView.ViewHolder, View> getForegroundView;
    private ButtonState buttonShowedState = ButtonState.GONE;
    private boolean swipeBack = false;
    private int buttonWidth = 300;


    public SwipeTouchHelper(int dragDirs, int swipeDirs, SwipeTouchHelperListener swipedListener, Function<RecyclerView.ViewHolder, View> getForegroundView) {
        super(dragDirs, swipeDirs);
        this.swipedListener = swipedListener;
        this.getForegroundView = getForegroundView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
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
        swipedListener.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = getForegroundView.apply(viewHolder);
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState == ButtonState.GONE) {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                if (buttonShowedState == ButtonState.VISIBLE) dX = Math.min(dX, -buttonWidth);
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonState.GONE) {
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }

    }

    private void setTouchListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((view, motionEvent) -> {
            // set swipeBack flag
            swipeBack = motionEvent.getAction() == MotionEvent.ACTION_UP
                    || motionEvent.getAction() == MotionEvent.ACTION_CANCEL;

            if (swipeBack) {
                // check if swipe distance is passed threshold
                // and make button visible if true
                if (dX < -buttonWidth) {
                    buttonShowedState = ButtonState.VISIBLE;

                    // set touch down listener on recycler view
                    // and prevent click on recycler items
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    setItemsClickable(recyclerView, false);
                }
            }
            return false;
        });
    }

    private void setTouchDownListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            return false;
        });
    }

    private void setTouchUpListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                final View foregroundView = getForegroundView.apply(viewHolder);
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, 0F, dY, actionState, isCurrentlyActive);

                // reset touch listener
                recyclerView.setOnTouchListener((v1, event1) -> false);

                setItemsClickable(recyclerView, true);
                buttonShowedState = ButtonState.GONE;
                swipeBack = false;
            }
            return false;
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        } else {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }
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
        void onSwiped(RecyclerView.ViewHolder viewHolder, int position);
    }
}