package com.tangrun.kits.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tangrun.kits.R;
import com.tangrun.kits.adapter.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author RainTang
 */
public class ImageGridView extends RecyclerView {

    private boolean addable;
    private boolean clearable;
    private boolean draggable;
    private int maxCount;
    private int addIcon;

    private GridLayoutManager manager;
    private ConcatAdapter concatAdapter;
    private ImageGridViewAdapter<?> adapter;
    private ImageGridViewAddAdapter addAdapter;

    public ImageGridView(Context context) {
        this(context, null);
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOverScrollMode(OVER_SCROLL_NEVER);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.ImageGridView);
        clearable = typedArray.getBoolean(R.styleable.ImageGridView_clearable, false);
        addable = typedArray.getBoolean(R.styleable.ImageGridView_addable, false);
        draggable = typedArray.getBoolean(R.styleable.ImageGridView_draggable, false);
        maxCount = typedArray.getInteger(R.styleable.ImageGridView_maxCount, 9);
        addIcon = typedArray.getResourceId(R.styleable.ImageGridView_addIcon, R.drawable.kits_baseline_add_box_24);

        super.setLayoutManager(manager = new GridLayoutManager(context, typedArray.getInteger(R.styleable.ImageGridView_spanCount, 3)));

        adapter = new ImageGridViewAdapter<>();
        addAdapter = new ImageGridViewAddAdapter();
        super.setAdapter(concatAdapter = new ConcatAdapter(adapter, addAdapter));
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            int lastCount = -1;

            @Override
            public void onChanged() {
                int dataListSize = adapter.getDataListSize();
                if (dataListSize == lastCount) {
                    return;
                }
                lastCount = dataListSize;
                resetAdd();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onChanged();
            }
        });

        typedArray.recycle();

        if (isInEditMode()) {
            setOnImageLoadListener(new OnImageLoadListener<Integer>() {
                @Override
                public void onLoad(ImageView imageView, Integer data) {
                    imageView.setImageResource(data);
                }
            });
            int integer = typedArray.getInteger(R.styleable.ImageGridView_itemCount, 1);
            List<Integer> imageList = new ArrayList<>();
            for (int i = 0; i < integer; i++) {
                imageList.add(R.drawable.kits_baseline_image_24);
            }
            getListAdapter().addAll(imageList);
        }
    }

    private void resetAdd() {
        if (addable && adapter.getDataList().size() < getMaxCount()) {
            if (!concatAdapter.getAdapters().contains(addAdapter)) {
                concatAdapter.addAdapter(addAdapter);
            }
        } else {
            concatAdapter.removeAdapter(addAdapter);
        }
    }

    @Override
    public void setAdapter(@Nullable @org.jetbrains.annotations.Nullable Adapter adapter) {

    }

    @Override
    public void setLayoutManager(@Nullable @org.jetbrains.annotations.Nullable LayoutManager layout) {

    }

    public <T> ListAdapter<T, ?> getListAdapter() {
        return (ListAdapter<T, ?>) adapter;
    }

    //region listener


    private OnAddImageListener onAddImageListener;
    private OnPreviewImageListener onPreviewImageListener;
    private OnImageLoadListener onImageLoadListener;

    public <T> OnAddImageListener<T> getOnAddImageListener() {
        return onAddImageListener;
    }

    public <T> void setOnAddImageListener(OnAddImageListener<T> onAddImageListener) {
        this.onAddImageListener = onAddImageListener;
    }

    public <T> OnPreviewImageListener<T> getOnPreviewImageListener() {
        return onPreviewImageListener;
    }

    public <T> void setOnPreviewImageListener(OnPreviewImageListener<T> onPreviewImageListener) {
        this.onPreviewImageListener = onPreviewImageListener;
    }

    public <T> OnImageLoadListener<T> getOnImageLoadListener() {
        return onImageLoadListener;
    }

    public void setOnImageLoadListener(OnImageLoadListener<?> onImageLoadListener) {
        this.onImageLoadListener = onImageLoadListener;
    }

    public interface OnAddImageListener<T> {
        void onAddImage(ImageGridView view, List<T> selectedList);
    }

    public interface OnPreviewImageListener<T> {
        void onPreviewImage(ImageGridView view, List<T> imageList, int position);
    }

    public interface OnImageLoadListener<T> {
        void onLoad(ImageView imageView, T data);
    }

    //endregion

    //region getter setter

    public boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    public boolean isClearable() {
        return clearable;
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getAddIcon() {
        return addIcon;
    }

    public void setAddIcon(int addIcon) {
        this.addIcon = addIcon;
        addAdapter.setList(Collections.singletonList(addIcon));

    }

    //endregion
}
