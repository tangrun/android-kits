package com.tangrun.kits.page;

public interface IPageable extends IPageQuery{

    int getPageSize();

    int getPageIndex();

    void setLoadFail();

    void setLoadSuccess(int size);
}
