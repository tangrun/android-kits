package com.tangrun.kits.page;

public abstract class PagerImpl implements IPager {
    protected int size = 15;
    protected int page = 1;
    protected boolean hasMore = false;
    protected boolean loading = false;
    protected boolean isLoadMore = false;

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public int getPageIndex() {
        return page;
    }

    @Override
    public String getQueryContent() {
        IPageQuery pagerQuery = getPagerQuery();
        return pagerQuery != null ? pagerQuery.getQueryContent() : null;
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean startRefresh() {
        if (loading) return false;
        loading = true;
        isLoadMore = false;
        hasMore = false;
        page = 1;
        IPageLoader pagerLoader = getPagerLoader();
        if (pagerLoader != null) pagerLoader.onPageLoad(true,this);
        return true;
    }

    @Override
    public boolean startLoadMore() {
        if (loading || !hasMore) return false;
        loading = true;
        isLoadMore = true;
        IPageLoader pagerLoader = getPagerLoader();
        if (pagerLoader != null) pagerLoader.onPageLoad(false,this);
        return true;
    }

    @Override
    public void setLoadFail() {
        loading = false;
        onLoadFinal(isLoadMore, hasMore);
    }

    @Override
    public void setLoadSuccess(int size) {
        loading = false;
        if (size > 0) {
            page++;
            hasMore = size == this.size;
        }
        onLoadFinal(isLoadMore, hasMore);
    }

    public abstract void onLoadFinal(boolean isLoadMore, boolean hasMore);

    public abstract IPageQuery getPagerQuery();

    public abstract IPageLoader getPagerLoader();

}
