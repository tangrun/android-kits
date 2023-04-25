package com.tangrun.kits.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tangrun.kits.R;
import com.tangrun.kits.adapter.IListAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author RainTang
 */
public class ImageGridView<T> extends RecyclerView implements IListAdapter<T>{

    private boolean addable;
    private boolean clearable;
    private boolean draggable;
    private int maxCount;
    private int addIcon;

    private GridLayoutManager manager;
    private ConcatAdapter concatAdapter;
    private ImageGridViewAdapter<T> adapter;
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
        adapter = new ImageGridViewAdapter();
        addAdapter = new ImageGridViewAddAdapter();
        super.setAdapter(concatAdapter = new ConcatAdapter(adapter,addAdapter));
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            int lastCount = -1;
            @Override
            public void onChanged() {
                int dataListSize = adapter.getDataListSize();
                if (dataListSize == lastCount) {
                    return;
                }
                lastCount = dataListSize;
                if (dataListSize >= getMaxCount()){
                    concatAdapter.removeAdapter(addAdapter);
                }else {
                    concatAdapter.addAdapter(addAdapter);
                }
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

        setOverScrollMode(OVER_SCROLL_NEVER);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.ImageGridView);
        setClearable(typedArray.getBoolean(R.styleable.ImageGridView_clearable, false));
        setAddable(typedArray.getBoolean(R.styleable.ImageGridView_addable, false));
        setDraggable(typedArray.getBoolean(R.styleable.ImageGridView_draggable, false));
        setMaxCount(typedArray.getInteger(R.styleable.ImageGridView_maxCount, 9));
        setAddIcon(typedArray.getResourceId(R.styleable.ImageGridView_addIcon, R.drawable.kits_baseline_add_box_24));

        super.setLayoutManager(manager = new GridLayoutManager(context, typedArray.getInteger(R.styleable.ImageGridView_spanCount, 3)));

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
            addAll((List<? extends T>) imageList);
        }
    }

    @Override
    public void setAdapter(@Nullable @org.jetbrains.annotations.Nullable Adapter adapter) {

    }

    @Override
    public void setLayoutManager(@Nullable @org.jetbrains.annotations.Nullable LayoutManager layout) {

    }

    //region adapter

    @Override
    public List<T> getDataList() {
        return adapter.getDataList();
    }

    @Override
    public void move(int fromPosition, int toPosition, boolean notifyChanged) {
        adapter.move(fromPosition, toPosition, notifyChanged);
    }

    @Override
    public void update(int position, T data) {
        adapter.update(position, data);
    }

    @Override
    public void setList(@NonNull @NotNull List<? extends T> list) {
        adapter.setList(list);
    }

    @Override
    public void remove(int position, boolean notifyChanged) {
        adapter.remove(position, notifyChanged);
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void addAll(@NonNull @NotNull List<? extends T> list) {
        adapter.addAll(list);
    }

    @Override
    public void addAll(int position, @NonNull @NotNull List<? extends T> list, boolean notifyChanged) {
        adapter.addAll(position, list, notifyChanged);
    }
    //endregion

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

    public interface OnAddImageListener<T>{
        void onAddImage(ImageGridView view, List<T> selectedList);
    }

    public interface OnPreviewImageListener<T>{
        void onPreviewImage(ImageGridView view, List<T> imageList, int position);
    }

    public interface OnImageLoadListener<T>{
        void onLoad(ImageView imageView,T data);
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
