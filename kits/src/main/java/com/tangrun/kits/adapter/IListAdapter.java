package com.tangrun.kits.adapter;

import androidx.annotation.NonNull;

import java.util.List;

public interface IListAdapter<D> {

    /**
     * 通知刷新位置有变化的item默认值
     * @return
     */
    default boolean isDefaultNotifyChange(){
        return false;
    }
    
    /**
     * 数据源
     * @return
     */
    List<D> getDataList();

    /**
     * 数据源大小
     * @return
     */
    default int getDataListSize(){
        return getDataList().size();
    }
    
    default void move(int fromPosition, int toPosition){
        move(fromPosition, toPosition, isDefaultNotifyChange());
    }

    /**
     * 移动item 
     * @param fromPosition
     * @param toPosition
     * @param notifyChanged 通知刷新位置有变化的item
     */
    void move(int fromPosition, int toPosition, boolean notifyChanged);

    /**
     * 更新item数据
     * @param position 
     * @param data 新的数据
     */
    void update(int position,D data);

    /**
     * 设置新的数据源 将移除旧的数据源 
     * @param list 新的数据源
     */
    void setList(@NonNull List<? extends D> list);

    default void remove(int position) {
        remove(position, isDefaultNotifyChange());
    }

    /**
     * 移除item
     * @param position 位置
     * @param notifyChanged 通知刷新位置有变化的item
     */
    void remove(int position, boolean notifyChanged);

    /**
     * 
     */
    void clear();

    void addAll(@NonNull List<? extends D> list);


    default void addAll(int position, @NonNull List<? extends D> list) {
        addAll(position, list, isDefaultNotifyChange());
    }

    /**
     * 指定位置插入数据
     * @param position 插入位置 必须是已存在的
     * @param list 插入的数据
     * @param notifyChanged 通知刷新位置有变化的item
     */
    void addAll(int position, @NonNull List<? extends D> list, boolean notifyChanged);

}
