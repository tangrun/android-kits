package com.tangrun.kits.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.tangrun.kits.R;
import org.jetbrains.annotations.NotNull;


public class ScrollableViewPager extends ViewPager {

    private boolean scrollable = true;

    public ScrollableViewPager(@NonNull @NotNull Context context) {
        this(context,null);
    }

    public ScrollableViewPager(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.ScrollableViewPager);
        if (a.hasValue(R.styleable.ScrollableViewPager_scrollable)) {
            setScrollable(a.getBoolean(R.styleable.ScrollableViewPager_scrollable, true));
        }
        a.recycle();
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scrollable && super.onInterceptTouchEvent(ev);
    }

}
