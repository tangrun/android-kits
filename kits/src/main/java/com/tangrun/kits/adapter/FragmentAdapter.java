package com.tangrun.kits.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter<T extends Fragment> extends FragmentStatePagerAdapter {
    public FragmentAdapter(@NonNull @NotNull FragmentManager fm) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public FragmentAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    private final List<T> fragmentList = new ArrayList<>();
    private final List<CharSequence> fragmentTitleList = new ArrayList<>();
    @Nullable
    private ViewPager viewPager;
    private boolean autoMaxOffscreenPageLimit;

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    /**
     * 添加 Fragment
     */
    public void addFragment(T fragment) {
        addFragment(fragment, null);
    }

    public void addFragment(T fragment, CharSequence title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
        refreshOffscreenPageLimit();
    }

    /**
     * 获取当前的Fragment
     */
    public T getCurrentFragment() {
        return fragmentList.get(viewPager.getCurrentItem());
    }

    /**
     * fragmen懒加载 设置最大
     * @param b
     */
    public void setAutoMaxOffscreenPageLimit(boolean b) {
        autoMaxOffscreenPageLimit = b;
        refreshOffscreenPageLimit();
    }

    private void refreshOffscreenPageLimit() {
        if (viewPager ==null){
            return;
        }
        if (autoMaxOffscreenPageLimit) {
            viewPager.setOffscreenPageLimit(getCount());
        }
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
        if (container instanceof ViewPager) {
            viewPager = (ViewPager) container;
        }
    }

}
