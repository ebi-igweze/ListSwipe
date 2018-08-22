package com.igweze.ebi.wafermessenger.ui;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;


enum ButtonState {
    GONE,
    VISIBLE
}

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SwipeTouchHelperListener swipedListener;
    private ButtonState buttonShowedState = ButtonState.GONE;
    private Runnable resetPreviousViewHolder = null;
    private boolean swipeBack = false;
    private float buttonWidth;

    public SwipeTouchHelper(float buttonWidth, SwipeTouchHelperListener swipedListener) {
        super(0, ItemTouchHelper.LEFT);
        this.swipedListener = swipedListener;
        this.buttonWidth = buttonWidth;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            // check if there is current visible button
            if (buttonShowedState == ButtonState.VISIBLE && resetPreviousViewHolder != null) {
                // reset previous view holder state
                resetPreviousViewHolder.run();
                resetPreviousViewHolder = null;
            }

            final View foregroundView = getForegroundView(viewHolder);
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
        final View foregroundView = getForegroundView(viewHolder);
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState == ButtonState.GONE) {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                dX = Math.min(dX, -buttonWidth);
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonState.GONE) {
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
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
        if (buttonShowedState == ButtonState.GONE) {
            final View foregroundView = getForegroundView(viewHolder);
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = getForegroundView(viewHolder);
        getDefaultUIUtil().clearView(foregroundView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View.OnTouchListener touchListener = (view, motionEvent) -> {
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
        };

        // set touch listener for recyclerView
        recyclerView.setOnTouchListener(touchListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View.OnTouchListener touchDownListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            return false;
        };
        // set touch listener as touchDown listener
        recyclerView.setOnTouchListener(touchDownListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        resetPreviousViewHolder = () -> {
            final View foregroundView = getForegroundView(viewHolder);
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, 0F, dY, actionState, isCurrentlyActive);

            // reset touch listener
            recyclerView.setOnTouchListener((v1, event1) -> false);
            // reset recycler view items to clickable
            setItemsClickable(recyclerView, true);
            // reset button state and swipe back
            buttonShowedState = ButtonState.GONE;
            swipeBack = false;
        };

        View.OnTouchListener touchUpListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // get buttonState before reset
                ButtonState currentState = buttonShowedState;
                // reset previous if not null
                if (resetPreviousViewHolder != null) resetPreviousViewHolder.run();

                View itemView = viewHolder.itemView;
                // create bounding rectangle, the size of the visible button (in background)
                RectF buttonRect = new RectF(itemView.getRight() - buttonWidth, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                // check if up event was within bounding rectangle
                if (buttonRect.contains(event.getX(), event.getY()) && currentState == ButtonState.VISIBLE) {
                    // initiate onSwiped on listener
                    swipedListener.onSwiped(viewHolder, viewHolder.getAdapterPosition());
                }
            }
            return false;
        };

        // set touch listener to touchUp listener
        recyclerView.setOnTouchListener(touchUpListener);
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private View getForegroundView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof SwipeTouchViewHolder) {
            return ((SwipeTouchViewHolder) viewHolder).getForegroundView();
        } else {
            throw new IllegalArgumentException("viewHolder must be instance of 'SwipeTouchViewHolder'");
        }
    }

    // swiped listener interface
    public interface SwipeTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int position);
    }

    public static abstract class SwipeTouchViewHolder extends RecyclerView.ViewHolder {
        public SwipeTouchViewHolder(View view) {
            super(view);
        }

        public abstract View getForegroundView();
    }
}