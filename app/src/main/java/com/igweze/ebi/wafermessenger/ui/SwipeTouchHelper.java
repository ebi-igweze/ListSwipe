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
    HIDDEN,
    VISIBLE
}

enum ButtonTransition {
    NONE,
    CLOSING,
    OPENING
}

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SwipeTouchHelperListener swipedListener;
    private ButtonState buttonState = ButtonState.HIDDEN;
    private RecyclerView.ViewHolder currentViewHolder = null;
    private Runnable cancelCurrentSwipe = null;
    private float buttonWidth;
    private ButtonTransition buttonTransition = ButtonTransition.NONE;

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
            if (viewHolder != currentViewHolder && buttonState == ButtonState.VISIBLE && cancelCurrentSwipe != null) {
                // reset previous view holder state
                cancelCurrentSwipe.run();
                cancelCurrentSwipe = null;
            }

            // set current view holder
            if (currentViewHolder != viewHolder) currentViewHolder = viewHolder;

            final View foregroundView = getForegroundView(viewHolder);
            // detect ui changes for only foreground view
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return buttonWidth;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 3;
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
            // if button is not currently visible, and swipe has passed button threshold
            if (buttonTransition == ButtonTransition.NONE  && buttonState == ButtonState.HIDDEN && dX < -buttonWidth) {
                buttonTransition = ButtonTransition.OPENING;
                // set touch up listener on recycler view
                setTouchUpListener(c, recyclerView, viewHolder);
            } else if (buttonTransition == ButtonTransition.OPENING) {
                float swipeTo = dX;
                if (buttonState == ButtonState.HIDDEN) {
                    // set min distance to button width
                    swipeTo = Math.min(swipeTo, -buttonWidth);
                    // button is now visible (transition complete)
                    if (swipeTo == -buttonWidth && dX == 0.0f)
                        buttonState = ButtonState.VISIBLE;
                } else if (buttonState == ButtonState.VISIBLE) {
                    // swipe displacement combined with button width
                    swipeTo = dX - buttonWidth;
                    swipeTo = Math.min(swipeTo, -buttonWidth);
                }

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, swipeTo, dY, actionState, isCurrentlyActive);
            } else if (buttonTransition == ButtonTransition.CLOSING) {
                if (dX == 0.0f) {
                    buttonState = ButtonState.HIDDEN;
                    buttonTransition = ButtonTransition.NONE;
                }
                // swipe displacement (combined with button width)
                float swipeTo = -buttonWidth + dX;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, swipeTo, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonState == ButtonState.HIDDEN && buttonTransition == ButtonTransition.NONE) {
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = getForegroundView(viewHolder);
        getDefaultUIUtil().clearView(foregroundView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // set previous swipe
        cancelCurrentSwipe = () -> {
            final View foregroundView = getForegroundView(viewHolder);
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, 0, 0, ACTION_STATE_SWIPE, false);
            // reset touch listener
            recyclerView.setOnTouchListener((v1, event1) -> false);
            // begin close button transition
            buttonTransition = ButtonTransition.CLOSING;
        };

        // touch listener for touch up events.
        View.OnTouchListener touchUpListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && buttonState == ButtonState.VISIBLE && buttonTransition == ButtonTransition.OPENING) {
                // get itemView to calculate button view bounds
                View itemView = viewHolder.itemView;
                // calculate bounding rectangle, the size of the visible button (in background)
                RectF foregroundRect = new RectF(itemView.getLeft() - buttonWidth, itemView.getTop(), itemView.getRight() - buttonWidth, itemView.getBottom());
                // calculate bounding rectangle, the size of the visible button (in background)
                RectF buttonRect = new RectF(itemView.getRight() - buttonWidth, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                // check if up event was within bounding rectangle
                if (foregroundRect.contains(event.getX(), event.getY())) {
                    // cancel current swipe
                    cancelCurrentSwipe.run();
                    // reset current swipe
                    cancelCurrentSwipe = null;
                } else if (buttonRect.contains(event.getX(), event.getY())) {
                    // cancel current swipe
                    cancelCurrentSwipe.run();
                    // reset current swipe
                    cancelCurrentSwipe = null;
                    // initiate onSwiped on listener
                    swipedListener.onSwiped(viewHolder, viewHolder.getAdapterPosition());
                }
            }
            return false;
        };

        // set touch listener to touchUp listener
        recyclerView.setOnTouchListener(touchUpListener);
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