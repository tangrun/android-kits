package com.tangrun.kits.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RainTang
 */
public abstract class ListAdapter<D, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements IListAdapter<D> {

    protected final List<D> dataList = new ArrayList<>();


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //region IListAdapter

    @Override
    @NonNull
    public List<D> getDataList() {
        return dataList;
    }

    @Override
    public int getDataListSize() {
        return dataList.size();
    }

    @Override
    public void move(int fromPosition, int toPosition, boolean notifyChange) {
        rangeCheck(fromPosition);
        rangeCheck(toPosition);
        dataList.add(toPosition, dataList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
        if (notifyChange) {
            int min = Math.min(fromPosition, toPosition);
            notifyItemRangeChanged(min, getItemCount() - min);
        }
    }

    @Override
    public void update(int position,D data) {
        rangeCheck(position);
        dataList.set(position, data);
        notifyItemChanged(position);
    }

    @Override
    public void setList(@NonNull List<? extends D> list) {
        int oldSize = dataList.size(), newSize = list.size();
        dataList.clear();
        if (oldSize!=0) {
            notifyItemRangeRemoved(0, oldSize);
        }
        dataList.addAll(list);
        if (newSize !=0){
            notifyItemRangeInserted(0,newSize);
        }
//        if (oldSize > newSize) {
//            notifyItemRangeRemoved(newSize, oldSize - newSize);
//            notifyItemRangeChanged(0, newSize);
//        } else if (oldSize < newSize) {
//            notifyItemRangeChanged(0, oldSize);
//            notifyItemRangeInserted(oldSize, newSize - oldSize);
//        } else {
//            notifyItemRangeChanged(0, newSize);
//        }
    }

    @Override
    public void remove(int position, boolean notifyChanged) {
        rangeCheck(position);
        dataList.remove(position);
        notifyItemRemoved(position);
        if (notifyChanged) {
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    @Override
    public void addAll(@NonNull List<? extends D> list) {
        int position = dataList.isEmpty() ? 0 : dataList.size();
        int count = list.size();
        dataList.addAll(list);
        notifyItemRangeInserted(position, count);
    }


    @Override
    public void clear() {
        if (dataList.isEmpty()) {
            return;
        }
        int count = dataList.size();
        dataList.clear();
        notifyItemRangeRemoved(0, count);
    }

    @Override
    public void addAll(int position,@NonNull  List<? extends D> list, boolean notifyChanged) {
        rangeCheck(position);
        dataList.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
        if (notifyChanged) {
            int positionStart = position + list.size();
            notifyItemRangeChanged(positionStart,getItemCount() - positionStart);
        }
    }

    //endregion

    private void rangeCheck(int index) {
        int size = getDataListSize();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + " size " + size);
        }
    }
}
