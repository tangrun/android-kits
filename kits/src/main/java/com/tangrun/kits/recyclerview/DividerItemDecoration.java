package com.tangrun.kits.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * StaggeredGridLayoutManager时DIVIDER_END 是 DIVIDER_MIDDLE 的效果
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "DividerItemDecoration1";

    public static final int DIVIDER_NONE = 0;
    public static final int DIVIDER_BEGIN = 1;
    public static final int DIVIDER_MIDDLE = 1 << 1;
    public static final int DIVIDER_END = 1 << 2;
    public static final int DIVIDER_ALL = DIVIDER_BEGIN | DIVIDER_MIDDLE | DIVIDER_END;

    private final Context context;

    public DividerItemDecoration(Context context) {
        this.context = context;
    }

    private int showDividerVertical = DIVIDER_NONE;
    private int showDividerHorizontal = DIVIDER_NONE;
    private Drawable verticalBeginDrawable, verticalMiddleDrawable, verticalEndDrawable;
    private Drawable horizontalBeginDrawable, horizontalMiddleDrawable, horizontalEndDrawable;

    private final Rect mBounds = new Rect();

    @Override
    public void onDrawOver(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void onDraw(@NonNull @NotNull Canvas canvas, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        if (showDividerVertical == DIVIDER_NONE && showDividerHorizontal == DIVIDER_NONE) return;
        canvas.save();
        final int left;
        final int right;
        final int top;
        final int bottom;

        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, top, right, bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
            left = 0;
            right = parent.getWidth();
        }

        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            int spanCount = staggeredGridLayoutManager.getSpanCount();
            int[] first = new int[spanCount];
            int[] last = new int[spanCount];
            staggeredGridLayoutManager.findLastVisibleItemPositions(last);
            staggeredGridLayoutManager.findFirstVisibleItemPositions(first);
//            Log.d(TAG, "getLayoutManagerInfo: " + Arrays.toString(first) + " " + Arrays.toString(last));
            staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(last);
            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(first);
//            Log.d(TAG, "getLayoutManagerInfo c: " + Arrays.toString(first) + " " + Arrays.toString(last));
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

//            final int bottom1 = mBounds.bottom + Math.round(child.getTranslationY());
//            final int top1 = bottom - mDivider.getIntrinsicHeight();
        }

        canvas.restore();
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state) {
        // 只在第一项画top和left 为了方便计算 position从1算
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            getItemOffsetsForGrid(outRect, view, parent, state, ((GridLayoutManager) parent.getLayoutManager()).getOrientation(), ((GridLayoutManager) parent.getLayoutManager()).getSpanCount());
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            getItemOffsetsForGrid(outRect, view, parent, state, ((LinearLayoutManager) parent.getLayoutManager()).getOrientation(), 1);
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            getItemOffsetsForStaggeredGridLayoutManager(outRect, view, parent, state, (StaggeredGridLayoutManager) parent.getLayoutManager());
        }
//        Log.d(TAG, "getItemOffsets: position: " + parent.getChildAdapterPosition(view) + " \noutRect: " + outRect);
    }

    /**
     * @return int[4] ints = new int[]{columnCount, columnPosition, rowCount, rowPosition};
     */
    private int[] getColRowInfo(int orientation, int spanCount, int itemCount, int itemPosition) {
        int columnCount, columnPosition, rowCount, rowPosition;
        if (orientation == RecyclerView.VERTICAL) {
            columnCount = spanCount;
            columnPosition = itemPosition % columnCount;
            rowCount = (int) Math.ceil(1.0f * itemCount / columnCount);
            rowPosition = (int) Math.floor(1.0f * itemPosition / columnCount);
        } else if (orientation == RecyclerView.HORIZONTAL) {
            rowCount = spanCount;
            rowPosition = itemPosition % rowCount;
            columnCount = (int) Math.ceil(1.0f * itemCount / rowCount);
            columnPosition = (int) Math.floor(1.0f * itemPosition / rowCount);
        } else {
            throw new RuntimeException("no support orientation: " + orientation);
        }
        return new int[]{columnCount, columnPosition, rowCount, rowPosition};
    }

    private boolean contains(int[] values, int value) {
        for (int i : values) {
            if (value == i) return true;
        }
        return false;
    }

    private void getItemOffsetsForStaggeredGridLayoutManager(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state, StaggeredGridLayoutManager manager) {
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

        int itemCount = state.getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        int spanCount = manager.getSpanCount();
        int spanIndex = layoutParams.getSpanIndex();
        int[] first = new int[spanCount];
        int[] last = new int[spanCount];

        manager.findFirstCompletelyVisibleItemPositions(first);
        manager.findLastCompletelyVisibleItemPositions(last);

        boolean top, bottom, left, right;
        if (manager.getOrientation() == RecyclerView.VERTICAL) {
            left = (spanIndex == 0);
            right = spanIndex == (spanCount - 1);
            top = first[0] == RecyclerView.NO_POSITION ? itemPosition < spanCount : contains(first, itemPosition);
            bottom = contains(last, itemPosition);
        } else if (manager.getOrientation() == RecyclerView.HORIZONTAL) {
            top = spanIndex == 0;
            bottom = spanIndex == spanCount - 1;
            left = first[0] == RecyclerView.NO_POSITION ? itemPosition < spanCount : contains(first, itemPosition);
            right = contains(last, itemPosition);
        } else {
            throw new RuntimeException("no support orientation: " + manager.getOrientation());
        }

        outRect.left = getDrawableWidth(left && hasFlag(showDividerHorizontal, DIVIDER_BEGIN) ? horizontalBeginDrawable : null);
        outRect.top = getDrawableHeight(top && hasFlag(showDividerVertical, DIVIDER_BEGIN) ? verticalBeginDrawable : null);
        outRect.right = getDrawableWidth(right ? hasFlag(showDividerHorizontal, DIVIDER_END) ? horizontalEndDrawable : null
                : hasFlag(showDividerHorizontal, DIVIDER_MIDDLE) ? horizontalMiddleDrawable : null);
        outRect.bottom = getDrawableHeight(bottom ? hasFlag(showDividerVertical, DIVIDER_END) ? verticalEndDrawable : null
                : hasFlag(showDividerVertical, DIVIDER_MIDDLE) ? verticalMiddleDrawable : null);
    }

    private void getItemOffsetsForGrid(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state, int orientation, int spanCount) {
        int itemCount = state.getItemCount(), itemPosition = parent.getChildAdapterPosition(view);
        int[] colRowInfo = getColRowInfo(orientation, 1, itemCount, itemPosition);
        int columnCount = colRowInfo[0], columnPosition = colRowInfo[1], rowCount = colRowInfo[2], rowPosition = colRowInfo[3];

        outRect.left = getDrawableWidth(columnPosition == 0 && hasFlag(showDividerHorizontal, DIVIDER_BEGIN) ? horizontalBeginDrawable : null);
        outRect.top = getDrawableHeight(rowPosition == 0 && hasFlag(showDividerVertical, DIVIDER_BEGIN) ? verticalBeginDrawable : null);
        outRect.right = getDrawableWidth(columnPosition == columnCount - 1 ? hasFlag(showDividerHorizontal, DIVIDER_END) ? horizontalEndDrawable : null
                : hasFlag(showDividerHorizontal, DIVIDER_MIDDLE) ? horizontalMiddleDrawable : null);
        outRect.bottom = getDrawableHeight(rowPosition == rowCount - 1 ? hasFlag(showDividerVertical, DIVIDER_END) ? verticalEndDrawable : null
                : hasFlag(showDividerVertical, DIVIDER_MIDDLE) ? verticalMiddleDrawable : null);
    }

    public DividerItemDecoration setShowDivider(@RecyclerView.Orientation int orientation, @Divider int divider) {
        switch (orientation) {
            case RecyclerView.VERTICAL:
                showDividerVertical = divider;
                break;
            case RecyclerView.HORIZONTAL:
                showDividerHorizontal = divider;
                break;
        }
        return this;
    }

    public DividerItemDecoration setDividerDrawable(@RecyclerView.Orientation int orientation, @Divider int divider, Drawable drawable) {

        switch (orientation) {
            case RecyclerView.VERTICAL:
                showDividerVertical = divider;
                break;
            case RecyclerView.HORIZONTAL:
                showDividerHorizontal = divider;
                break;
        }

        if (hasFlag(divider, DIVIDER_BEGIN)) {
            if (orientation == RecyclerView.VERTICAL) {
                verticalBeginDrawable = drawable;
            } else if (orientation == RecyclerView.HORIZONTAL) {
                horizontalBeginDrawable = drawable;
            }
        }
        if (hasFlag(divider, DIVIDER_MIDDLE)) {
            if (orientation == RecyclerView.VERTICAL) {
                verticalMiddleDrawable = drawable;
            } else if (orientation == RecyclerView.HORIZONTAL) {
                horizontalMiddleDrawable = drawable;
            }
        }
        if (hasFlag(divider, DIVIDER_END)) {
            if (orientation == RecyclerView.VERTICAL) {
                verticalEndDrawable = drawable;
            } else if (orientation == RecyclerView.HORIZONTAL) {
                horizontalEndDrawable = drawable;
            }
        }
        return this;
    }

    public DividerItemDecoration setDividerDrawableSize(@RecyclerView.Orientation int orientation, @Divider int divider, int dp) {
        return setDividerDrawableSize(orientation, divider, dp, TypedValue.COMPLEX_UNIT_DIP);
    }

    public DividerItemDecoration setDividerDrawableSize(@RecyclerView.Orientation int orientation, @Divider int divider, int value, @Dimension int unit) {
        int dimension = (int) TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
        return setDividerDrawable(orientation, divider, getDrawableForSize(dimension));
    }

    public DividerItemDecoration setDividerDrawableRes(@RecyclerView.Orientation int orientation, @Divider int divider, @DrawableRes int id) {
        return setDividerDrawable(orientation, divider, getDrawable(id));
    }

    @IntDef(flag = true, value = {DIVIDER_NONE, DIVIDER_BEGIN, DIVIDER_MIDDLE, DIVIDER_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Divider {
    }

    private static int getDrawableHeight(Drawable drawable) {
        return drawable == null ? 0 : drawable.getIntrinsicHeight();
    }

    private static int getDrawableWidth(Drawable drawable) {
        return drawable == null ? 0 : drawable.getIntrinsicWidth();
    }

    private static boolean hasFlag(int value, int flag) {
        return (value & flag) == flag;
    }

    private Drawable getDrawable(int drawableId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
    }

    private static Drawable getDrawableForSize(int width, int height) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(width, height);
        return drawable;
    }

    private static Drawable getDrawableForSize(int size) {
        return getDrawableForSize(size, size);
    }

}
