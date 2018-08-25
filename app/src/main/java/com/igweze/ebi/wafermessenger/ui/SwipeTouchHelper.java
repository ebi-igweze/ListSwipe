package com.igweze.ebi.wafermessenger.ui;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.END;

enum ButtonState {
    HIDDEN,
    VISIBLE
}

enum ButtonTransition {
    OPENING,
    OPENED,
    CLOSED
}

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ButtonTransition buttonTransition = ButtonTransition.CLOSED;
    private RecyclerView.ViewHolder currentViewHodler = null;
    private ButtonState buttonState = ButtonState.HIDDEN;
    private SwipeTouchHelperListener swipedListener;
    private Runnable cancelCurrentSwipe = null;
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
        return defaultValue * 5;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // reset state
        buttonTransition = ButtonTransition.CLOSED;
        buttonState = ButtonState.HIDDEN;
        cancelCurrentSwipe = null;
        // Row is swiped from recycler view
        swipedListener.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // get the foreground view, for swipe animation
        final View foregroundView = getForegroundView(viewHolder);

        if (actionState == ACTION_STATE_SWIPE) {
            float swipeTo = dX;
            // if button is not currently visible, and swipe has passed button threshold
            if (buttonTransition == ButtonTransition.CLOSED && dX < -buttonWidth) {
                // set the currentViewHolder
                currentViewHodler = viewHolder;
                // set transition to opening
                buttonTransition = ButtonTransition.OPENING;
                // set touch up listener on recycler view
                setTouchListener(recyclerView, viewHolder);
            } else if (buttonTransition == ButtonTransition.OPENING) {
                // set min distance to button width
                swipeTo = Math.min(swipeTo, -buttonWidth);
                // button is now visible (transition complete)
                if (swipeTo == -buttonWidth && dX == 0.0f) {
                    buttonState = ButtonState.VISIBLE;
                    buttonTransition = ButtonTransition.OPENED;
                }
            } else if (buttonTransition == ButtonTransition.OPENED && currentViewHodler == viewHolder) {
                // swipe displacement combined with opened button width
                swipeTo = dX - buttonWidth;
                swipeTo = Math.min(swipeTo, -buttonWidth);
            }

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, swipeTo, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = getForegroundView(viewHolder);
        getDefaultUIUtil().clearView(foregroundView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // get foreground view
        final View foregroundView = getForegroundView(viewHolder);

        // set previous swipe
        cancelCurrentSwipe = () -> {
            foregroundView.animate().translationX(0).setDuration(200).start();
            // close opened button
            buttonTransition = ButtonTransition.CLOSED;
            buttonState = ButtonState.HIDDEN;
            // reset current swipe
            cancelCurrentSwipe = null;
            // reset touch listener
            recyclerView.setOnTouchListener((v1, event1) -> false);
        };

        // get itemView to calculate button view bounds
        View itemView = viewHolder.itemView;

        foregroundView.setOnClickListener(view -> {
            // check if button is visible
            boolean buttonIsVisible = buttonState == ButtonState.VISIBLE;
            if (buttonIsVisible) {
                // the foreground view was tapped
                // so cancel current swipe
                cancelCurrentSwipe.run();
            }
            // reset click listener
            foregroundView.setOnClickListener(view1 -> {});
        });

        // touch listener for touch up events.
        View.OnTouchListener touchUpListener = (v, event) -> {
            int eventAction = event.getAction();
            boolean isActionDown = eventAction == MotionEvent.ACTION_DOWN;
            boolean isActionMove = eventAction == MotionEvent.ACTION_MOVE;
            boolean isActionPointerUp = eventAction == MotionEvent.ACTION_POINTER_UP;

            // check if button is visible
            boolean buttonIsVisible = buttonState == ButtonState.VISIBLE;

            if (buttonIsVisible) {
                if (isActionDown) {
                    // calculate bounding rectangle, the size of the visible button (in background)
                    RectF buttonRect = new RectF(itemView.getRight() - buttonWidth, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    // check if up event was within bounding rectangle
                    if (buttonRect.contains(event.getX(), event.getY())) {
                        // cancel current swipe
                        cancelCurrentSwipe.run();
                        // notify item has been swiped
                        onSwiped(viewHolder, END);
                    }
                }


                // calculate bounding rectangle, the size of the visible foreground view
                RectF foregroundRect = new RectF(itemView.getLeft() - buttonWidth, itemView.getTop(), itemView.getRight() - buttonWidth, itemView.getBottom());
                boolean isWithinForegroundView = foregroundRect.contains(event.getX(), event.getY());

                if (isActionDown || isActionMove || isActionPointerUp) {
                    if (!isWithinForegroundView && cancelCurrentSwipe != null) {
                        // a new item is being swiped
                        // so cancel current swipe
                        cancelCurrentSwipe.run();
                    }
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