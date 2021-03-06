package com.tangrun.kits.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tangrun.kits.R;

import java.util.ArrayList;
import java.util.List;

public class ImageGridView extends RecyclerView {
    private GridLayoutManager manager;
    private ImageGridViewAdapter adapter;

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
        super.setAdapter(adapter);

        setOverScrollMode(OVER_SCROLL_NEVER);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.ImageGridView);
        setCanClear(typedArray.getBoolean(R.styleable.ImageGridView_canClear, false));
        setCanAdd(typedArray.getBoolean(R.styleable.ImageGridView_canAdd, false));
        setCanDrag(typedArray.getBoolean(R.styleable.ImageGridView_canDrag, false));
        setMaxSize(typedArray.getInteger(R.styleable.ImageGridView_maxSize, 9));
        setAddItemImage(typedArray.getResourceId(R.styleable.ImageGridView_addItemImage, R.drawable.kits_baseline_add_box_24));
        setDragItemBackgroundColor(typedArray.getColor(R.styleable.ImageGridView_dragItemBackgroundColor, Color.parseColor("#E8E8E8")));

        manager = new GridLayoutManager(context, typedArray.getInteger(R.styleable.ImageGridView_spanCount, 3));
        super.setLayoutManager(manager);

        typedArray.recycle();

        if (isInEditMode()) {
            ImageGridViewAdapter.globalLoaderMap.put(Integer.class, new ImageLoader<Integer>() {
                @Override
                public void onLoad(ImageView imageView, Integer integer) {
                    imageView.setImageResource(integer);
                }
            });
            int integer = typedArray.getInteger(R.styleable.ImageGridView_itemCount, 1);
            List<Object> objectList = new ArrayList<>();
            for (int i = 0; i < integer; i++) {
                objectList.add(R.drawable.kits_baseline_image_24);
            }
            addImages(objectList);
        }
    }


    public void setDragItemBackgroundColor(int color) {
        adapter.dragItemBackgroundColor = color;
    }

    @Override
    public void setAdapter(@Nullable @org.jetbrains.annotations.Nullable Adapter adapter) {

    }

    @Override
    public void setLayoutManager(@Nullable @org.jetbrains.annotations.Nullable LayoutManager layout) {

    }

    public void setAddItemImage(Object image) {
        adapter.addItemImage = image;
    }

    public void setCanDrag(boolean canDrag) {
        adapter.canDrag = canDrag;
    }

    public void setMaxSize(int maxSize) {
        adapter.maxSize = maxSize;
    }

    public void setCanAdd(boolean canAdd) {
        adapter.canAdd = canAdd;
    }

    public void setCanClear(boolean canClear) {
        adapter.canClear = canClear;
    }

    public List<Object> getImageList() {
        return adapter.objectList;
    }

    public void removeImage(Object image) {
        int i = adapter.objectList.indexOf(image);
        if (i > -1) {
            removeImage(i);
        }
    }

    public void removeImage(int position) {
        adapter.removeImage(position);
    }

    public void setImages(List<Object> images) {
        adapter.objectList.clear();
        addImages(images);
    }

    public void addImages(List<Object> images) {
        adapter.addImages(images);
    }

    public void addImage(Object image) {
        adapter.addImage(image);
    }
}
