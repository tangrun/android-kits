package com.tangrun.kits.page;

public interface IPager extends IPageable{

    boolean hasMore();

    boolean isLoading();

    boolean startRefresh();

    boolean startLoadMore();

}
