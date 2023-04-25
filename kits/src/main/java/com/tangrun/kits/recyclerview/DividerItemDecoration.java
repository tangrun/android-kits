package com.tangrun.kits.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.*;
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

    private Drawable verticalBeginDrawable, verticalMiddleDrawable, verticalEndDrawable;
    private Drawable horizontalBeginDrawable, horizontalMiddleDrawable, horizontalEndDrawable;

    private final Rect mBounds = new Rect();

    @Override
    public void onDrawOver(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void onDraw(@NonNull @NotNull Canvas canvas, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {

    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            setItemOffsetsForGrid(outRect, view, parent, state, ((GridLayoutManager) parent.getLayoutManager()).getOrientation(), ((GridLayoutManager) parent.getLayoutManager()).getSpanCount());
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            setItemOffsetsForGrid(outRect, view, parent, state, ((LinearLayoutManager) parent.getLayoutManager()).getOrientation(), 1);
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            setItemOffsetsForStaggeredGridLayoutManager(outRect, view, parent, state, (StaggeredGridLayoutManager) parent.getLayoutManager());
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

    private void setItemOffsetsForStaggeredGridLayoutManager(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state, StaggeredGridLayoutManager manager) {
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

        int itemCount = state.getItemCount();
        int itemPosition = parent.getChildAdapterPosition(view);
        int spanCount = manager.getSpanCount();
        int spanIndex = layoutParams.getSpanIndex();
        int[] first = new int[spanCount];
        // 这里获取的一直是NO_POSITION，所以StaggeredGrid时没有end
        int[] last = new int[spanCount];

        manager.findFirstCompletelyVisibleItemPositions(first);
        manager.findLastCompletelyVisibleItemPositions(last);

        boolean top, bottom, left, right;
        if (manager.getOrientation() == RecyclerView.VERTICAL) {
            left = (spanIndex == 0);
            top = first[0] == RecyclerView.NO_POSITION ? itemPosition < spanCount : contains(first, itemPosition);
            right = spanIndex == (spanCount - 1);
            bottom = contains(last, itemPosition);
        } else if (manager.getOrientation() == RecyclerView.HORIZONTAL) {
            left = first[0] == RecyclerView.NO_POSITION ? itemPosition < spanCount : contains(first, itemPosition);
            top = spanIndex == 0;
            right = contains(last, itemPosition);
            bottom = spanIndex == spanCount - 1;
        } else {
            throw new RuntimeException("no support orientation: " + manager.getOrientation());
        }
        if (itemPosition == 39){
            Log.d(TAG, "setItemOffsetsForStaggeredGridLayoutManager: "+ Arrays.toString(last));
        }
        Log.d(TAG, "setItemOffsetsForStaggeredGridLayoutManager: "+itemPosition+left+" "+right+" "+top+" "+bottom);
        outRect.left = getDrawableWidth(left ? horizontalBeginDrawable : null);
        outRect.top = getDrawableHeight(top ? verticalBeginDrawable : null);
        outRect.right = getDrawableWidth(right ? horizontalEndDrawable : horizontalMiddleDrawable);
        outRect.bottom = getDrawableHeight(bottom ? verticalEndDrawable : verticalMiddleDrawable);
    }

    private void setItemOffsetsForGrid(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state, int orientation, int spanCount) {
        int itemCount = state.getItemCount(), itemPosition = parent.getChildAdapterPosition(view);
        int[] colRowInfo = getColRowInfo(orientation, spanCount, itemCount, itemPosition);
        int columnCount = colRowInfo[0], columnPosition = colRowInfo[1], rowCount = colRowInfo[2], rowPosition = colRowInfo[3];

        outRect.left = getDrawableWidth(columnPosition == 0 ? horizontalBeginDrawable : null);
        outRect.top = getDrawableHeight(rowPosition == 0 ? verticalBeginDrawable : null);
        outRect.right = getDrawableWidth(columnPosition == columnCount - 1 ? horizontalEndDrawable : horizontalMiddleDrawable);
        outRect.bottom = getDrawableHeight(rowPosition == rowCount - 1 ? verticalEndDrawable : verticalMiddleDrawable);
    }

    private void drawItemOffsetsForGrid(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, RecyclerView.State state, int orientation, int spanCount) {
        int itemCount = state.getItemCount(), itemPosition = parent.getChildAdapterPosition(view);
        int[] colRowInfo = getColRowInfo(orientation, spanCount, itemCount, itemPosition);
        int columnCount = colRowInfo[0], columnPosition = colRowInfo[1], rowCount = colRowInfo[2], rowPosition = colRowInfo[3];

        outRect.left = getDrawableWidth(columnPosition == 0 ? horizontalBeginDrawable : null);
        outRect.top = getDrawableHeight(rowPosition == 0 ? verticalBeginDrawable : null);
        outRect.right = getDrawableWidth(columnPosition == columnCount - 1 ? horizontalEndDrawable : horizontalMiddleDrawable);
        outRect.bottom = getDrawableHeight(rowPosition == rowCount - 1 ? verticalEndDrawable : verticalMiddleDrawable);
    }

    public DividerItemDecoration setDividerDrawable(@RecyclerView.Orientation int orientation, @Divider int divider, @Nullable Drawable drawable) {
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
