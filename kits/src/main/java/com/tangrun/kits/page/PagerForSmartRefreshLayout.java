package com.tangrun.kits.page;

import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import org.jetbrains.annotations.NotNull;

public class PagerForSmartRefreshLayout extends PagerImpl implements OnRefreshLoadMoreListener, LifecycleEventObserver {

    private final Builder builder;

    private PagerForSmartRefreshLayout(Builder builder) {
        this.builder = builder;

    }

    private void init() {
        builder.refreshLayout.setOnRefreshLoadMoreListener(this);
        builder.lifecycleOwner.getLifecycle().removeObserver(this);
        builder.lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void onLoadFinal(boolean isLoadMore, boolean hasMore) {
        if (builder.refreshLayout == null) return;
        if (isLoadMore) {
            if (hasMore)
                builder.refreshLayout.finishLoadMore();
            else
                builder.refreshLayout.finishLoadMoreWithNoMoreData();
        } else {
            if (hasMore)
                builder.refreshLayout.finishRefresh();
            else
                builder.refreshLayout.finishRefreshWithNoMoreData();
        }
    }

    @Override
    public IPageQuery getPagerQuery() {
        return builder.pagerQuery;
    }

    @Override
    public IPageLoader getPagerLoader() {
        return builder.pagerLoader;
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        if (!startLoadMore()) {
            setLoadFail();
        }
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        if (!startRefresh()) {
            setLoadFail();
        }
    }

    @Override
    public void onStateChanged(@NonNull @NotNull LifecycleOwner source, @NonNull @NotNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            if (builder.autoRefresh)
                if (builder.refreshLayout != null) {
                    builder.refreshLayout.autoRefresh();
                }
        }
    }

    public static class Builder {
        boolean autoRefresh;
        SmartRefreshLayout refreshLayout;
        IPageLoader pagerLoader;
        IPageQuery pagerQuery;
        LifecycleOwner lifecycleOwner;
        PagerForSmartRefreshLayout pager = new PagerForSmartRefreshLayout(this);

        public Builder(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
            autoRefresh = true;
        }

        /**
         * 设置自动刷新 默认true
         * @param autoRefresh
         * @return
         */
        public Builder setAutoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
            return this;
        }

        /**
         * 绑定刷新布局view
         * @param refreshLayout
         * @return
         */
        public Builder setRefreshLayout(SmartRefreshLayout refreshLayout) {
            this.refreshLayout = refreshLayout;
            return this;
        }

        /**
         * 设置数据加载回调
         * @param pagerLoader
         * @return
         */
        public Builder setPagerLoader(IPageLoader pagerLoader) {
            this.pagerLoader = pagerLoader;
            return this;
        }

        /**
         * 绑定输入框搜索内容 自动刷新
         * @param editText
         * @return
         */
        public Builder setDefaultPagerQuery(EditText editText) {
            pagerQuery = new PagerQueryForEdittext(pager, editText);
            return this;
        }

        /**
         * 设置搜索内容回调
         * @param function
         * @return
         */
        public Builder setPagerQuery(Function<IPager,IPageQuery>  function) {
            this.pagerQuery = function.apply(pager);
            return this;
        }

        public PagerForSmartRefreshLayout build() {
            pager.init();
            return pager;
        }
    }
}
