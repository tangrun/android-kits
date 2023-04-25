package com.tangrun.kits.adapter;

import android.util.Pair;
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

public class ListFragmentAdapter extends FragmentStatePagerAdapter {
    public ListFragmentAdapter(@NonNull @NotNull FragmentManager fm) {
        this(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public ListFragmentAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    private final List<Pair<Fragment, String>> fragmentList = new ArrayList<>();
    @Nullable
    private ViewPager viewPager;

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position).first;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(position).second;
    }

    /**
     * 添加 Fragment
     */
    public void add(Fragment fragment) {
        fragmentList.add(Pair.create(fragment, null));
    }

    /**
     * 添加 Fragment
     */
    public void add(Pair<Fragment, String> pair) {
        fragmentList.add(pair);
    }

    /**
     * 获取当前的Fragment
     */
    public Fragment getCurrentFragment() {
        return viewPager == null ? null : fragmentList.get(viewPager.getCurrentItem()).first;
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        super.startUpdate(container);
        if (container instanceof ViewPager) {
            viewPager = (ViewPager) container;
        }
    }

}
