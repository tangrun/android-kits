package com.tangrun.kits.image;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author RainTang
 */
class ImageGridViewDragCallback extends ItemTouchHelper.Callback {
    ImageGridView imageGridView;
    ImageGridViewAdapter adapter;

    public ImageGridViewDragCallback(ImageGridView imageGridView, ImageGridViewAdapter adapter) {
        this.imageGridView = imageGridView;
        this.adapter = adapter;
    }

    private RecyclerView.ViewHolder dragingViewHolder;

    private void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (dragingViewHolder != null) {
            onStopDrag();
        }
        dragingViewHolder = viewHolder;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0F, 1.1F),
                ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0F, 1.1F));
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.setDuration(50);
        animatorSet.start();
    }

    private void onStopDrag() {
        if (dragingViewHolder != null) {
            RecyclerView.ViewHolder viewHolder = dragingViewHolder;
            dragingViewHolder = null;

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1F, 1.0F),
                    ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1F, 1.0F));
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.setDuration(50);
            animatorSet.start();
        }
    }

    @Override
    public void onSelectedChanged(@Nullable @org.jetbrains.annotations.Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            onStartDrag(viewHolder);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            onStopDrag();
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


    @Override
    public boolean canDropOver(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder current, @NotNull RecyclerView.ViewHolder target) {
        return adapter == current.getBindingAdapter() && current.getBindingAdapter() == target.getBindingAdapter();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.move(viewHolder.getLayoutPosition(), target.getLayoutPosition(),false);
        return false;
    }

    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
