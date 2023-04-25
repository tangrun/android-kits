package com.tangrun.kits.page;

import android.text.TextUtils;

import java.util.List;

public class PageHelper {
    private static int sPageStart = 1, sPageSize = 12;
    private String query;
    private int pageStart;
    private int pageIndex;
    private int pageSize;
    private int currentSize = 0;
    private int totalSize = -1;


    public static PageHelper of() {
        return of(sPageStart, sPageSize);
    }

    public static PageHelper of(int pageStart, int size) {
        PageHelper pageHelper = new PageHelper();
        pageHelper.pageStart = pageStart;
        pageHelper.pageIndex = pageStart;
        pageHelper.pageSize = size;
        return pageHelper;
    }

    public static void setDefault(int pageStart, int pageSize) {
        sPageStart = pageStart;
        sPageSize = pageSize;
    }

    public void reset() {
        pageIndex = pageStart;
        totalSize = -1;
        currentSize = 0;
        query = null;
    }
    public PageHelper setLoadResult(boolean success, int size) {
        if (success) {
            pageIndex++;
            currentSize += size;
        }
        return this;
    }

    public PageHelper setLoadResult(boolean success, List<?> list) {
        return setLoadResult(success, list == null ? 0 : list.size());
    }

    public boolean hasMore() {
        if (totalSize >= 0) {
            return currentSize < totalSize;
        } else {
            return currentSize >= pageSize * (pageIndex - pageStart);
        }
    }

    public boolean isQueryEmpty(){
        return TextUtils.isEmpty(query);
    }

    //region getter setter


    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getQuery() {
        return query;
    }

    public PageHelper setQuery(String query) {
        this.query = query;
        return this;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public PageHelper setTotalSize(int totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    //endregion
}
