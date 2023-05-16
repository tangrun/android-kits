package com.tangrun.kits.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RainTang
 */
public abstract class ListAdapter<D, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final List<D> dataList = new ArrayList<>();


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //region

    @NonNull
    public List<D> getDataList() {
        return dataList;
    }

    public int getDataListSize() {
        return dataList.size();
    }

    public void notifyAllItemChanged() {
        if (dataList.size() == 0) return;
        notifyItemRangeChanged(0, dataList.size());
    }

    public void move(int fromPosition, int toPosition ) {
        checkExists(fromPosition);
        checkExists(toPosition);
        dataList.add(toPosition, dataList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    public void update(int position, D data) {
        checkExists(position);
        dataList.set(position, data);
        notifyItemChanged(position);
    }

    public void setList(@NonNull List<? extends D> list) {
        int oldSize = dataList.size(), newSize = list.size();
        dataList.clear();
        if (oldSize != 0) {
            notifyItemRangeRemoved(0, oldSize);
        }
        dataList.addAll(list);
        if (newSize != 0) {
            notifyItemRangeInserted(0, newSize);
        }
    }

    public void remove(int position ) {
        checkExists(position);
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public void add(D d) {
        add(dataList.size(), d);
    }


    public void add(int position, D d ) {
        checkAdd(position);
        dataList.add(position, d);
        notifyItemInserted(position);
    }

    public void addAll(@NonNull List<? extends D> list) {
        addAll(dataList.size(), list);
    }


    public void addAll(int position, @NonNull List<? extends D> list) {
        checkAdd(position);
        dataList.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
    }

    public void clear() {
        if (dataList.isEmpty()) {
            return;
        }
        int count = dataList.size();
        dataList.clear();
        notifyItemRangeRemoved(0, count);
    }

    //endregion

    private void checkAdd(int index) {
        int size = getDataListSize();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index " + index + " size " + size);
        }
    }

    private void checkExists(int index) {
        int size = getDataListSize();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + " size " + size);
        }
    }
}
