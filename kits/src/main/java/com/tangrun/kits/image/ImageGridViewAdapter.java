package com.tangrun.kits.image;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author RainTang
 */
class ImageGridViewAdapter<T> extends ImageGridViewBaseAdapter<T> {

    protected ItemTouchHelper itemTouchHelper;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        imageGridView = (ImageGridView) recyclerView;
        if (itemTouchHelper == null) {
            itemTouchHelper = new ItemTouchHelper(new ImageGridViewDragCallback(imageGridView, this));
        }
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageGridViewViewHolder viewHolder, int position) {
        ImageGridView.OnImageLoadListener<T> onImageLoadListener = imageGridView.getOnImageLoadListener();
        if (onImageLoadListener != null) {
            onImageLoadListener.onLoad(viewHolder.ivImg, getDataList().get(viewHolder.getBindingAdapterPosition()));
        }

        viewHolder.ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageGridView.OnPreviewImageListener<T> onPreviewImageListener = imageGridView.getOnPreviewImageListener();
                if (onPreviewImageListener != null) {
                    onPreviewImageListener.onPreviewImage(imageGridView, getDataList(), viewHolder.getBindingAdapterPosition());
                }
            }
        });
        viewHolder.ivImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (imageGridView.isDraggable()) {
                    itemTouchHelper.startDrag(viewHolder);
                    return true;
                }
                return false;
            }
        });
        viewHolder.ivClear.setVisibility(View.VISIBLE);
        viewHolder.ivClear.setOnClickListener(v -> remove(viewHolder.getBindingAdapterPosition()));

    }

    @Override
    public void addAll(int position, @NonNull List<? extends T> list) {
        checkSize(list, imageGridView.getMaxCount() - getDataListSize());
        super.addAll(position, list);
    }

    @Override
    public void setList(@NonNull List<? extends T> list) {
        checkSize(list, imageGridView.getMaxCount());
        super.setList(list);
    }

    @Override
    public void addAll(@NonNull List<? extends T> list) {
        checkSize(list, imageGridView.getMaxCount() - getDataListSize());
        super.addAll(list);
    }

    private void checkSize(List<?> list, int maxSize) {
        if (list.size() > maxSize) {
            throw new RuntimeException("list size(" + list.size() + ") is greater than the maximum(" + maxSize + ").");
        }
    }
}
