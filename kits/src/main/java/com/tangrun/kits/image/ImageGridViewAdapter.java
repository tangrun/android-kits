package com.tangrun.kits.image;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.tangrun.kits.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ImageGridViewAdapter extends RecyclerView.Adapter<ImageGridViewAdapter.ViewHolder> {
    static Map<Class<?>, ImageGridViewImageLoader<?>> globalLoaderMap = new HashMap<>();
    static ImageGridViewOnClickListener globalOnClickListener;


    public boolean canAdd;
    public boolean canClear;
    public boolean canDrag;
    public int maxSize;
    public Object addItemImage;
    public int dragItemBackgroundColor;
    public final List<Object> objectList = new ArrayList<>();
    public Map<Class<?>, ImageGridViewImageLoader<?>> loaderMap = new HashMap<>();
    public ImageGridViewOnClickListener onClickListener;
    ImageGridView imageGridView;
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

        @Override
        public boolean isLongPressDragEnabled() {
            Log.d("TAG", "isLongPressDragEnabled() called");
            return canDrag;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            Log.d("TAG", "isItemViewSwipeEnabled() called");
            return false;
        }

        @Override
        public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            stopDrag((ViewHolder) viewHolder);
            notifyDataSetChanged();
        }

        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            Log.d("TAG", "canDropOver() called with: recyclerView = [" + recyclerView + "], current = [" + current + "], target = [" + target + "]");
            return ImageGridViewAdapter.this.canDropOver(current.getLayoutPosition(), target.getLayoutPosition());
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.d("TAG", "getMovementFlags() called with: recyclerView = [" + recyclerView + "], viewHolder = [" + viewHolder + "]");
            return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.d("TAG", "onMove() called with: recyclerView = [" + recyclerView + "], viewHolder = [" + viewHolder + "], target = [" + target + "]");
            ImageGridViewAdapter.this.onMove(viewHolder.getLayoutPosition(), target.getLayoutPosition());
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("TAG", "onSwiped() called with: viewHolder = [" + viewHolder + "], direction = [" + direction + "]");
        }

    });

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        imageGridView = (ImageGridView) recyclerView;
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        itemTouchHelper.attachToRecyclerView(null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid_view, parent, false));
    }

    void startDrag(ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setBackgroundColor(dragItemBackgroundColor);
        }
    }

    void stopDrag(ViewHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.setBackgroundColor(0);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position1) {
        int position = position1;
        boolean isLastAddItem = showAddItem() && position == getAddItemPosition();


        Object obj = isLastAddItem ? addItemImage : objectList.get(position);
        ImageGridViewImageLoader loader = null;
        loader = loaderMap.get(obj.getClass());
        if (loader == null) {
            loader = globalLoaderMap.get(obj.getClass());
        }
        if (loader == null) {
            throw new RuntimeException("不支持的图片加载类型 " + obj.getClass());
        }
        loader.onLoad(holder.ivImg, obj);

        holder.ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageGridViewOnClickListener listener = null;
                if (onClickListener != null) {
                    listener = onClickListener;
                } else if (globalOnClickListener != null) {
                    listener = globalOnClickListener;
                }
                if (listener == null) return;
                if (isLastAddItem) {
                    listener.onAddImage(imageGridView, objectList, objectList.size(), maxSize);
                } else {
                    listener.onPreviewImage(imageGridView, objectList, holder.getLayoutPosition());
                }
            }
        });
        holder.ivImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (showAddItem() && getAddItemPosition() == holder.getLayoutPosition()) {
                    return true;
                } else {
                    startDrag(holder);
                }
                return true;
            }
        });

        holder.ivClear.setVisibility(canClear && !isLastAddItem ? View.VISIBLE : View.INVISIBLE);
        holder.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectList.remove(holder.getLayoutPosition());
                notifyItemRemoved(holder.getLayoutPosition());
            }
        });

    }

    boolean showAddItem() {
        return canAdd && objectList.size() < maxSize;
    }

    int getAddItemPosition() {
        return getItemCount() - 1;
    }

    @Override
    public int getItemCount() {
        int size = objectList.size();
        return showAddItem() ? size + 1 : size;
    }

    public void onMove(int fromPosition, int toPosition) {
        Log.d("TAG", "onMove() called with: fromPosition = [" + fromPosition + "], toPosition = [" + toPosition + "]");
        if (showAddItem() && toPosition == getItemCount() - 1)
            return;
        objectList.add(toPosition, objectList.remove(fromPosition));
//        Collections.swap(objectList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public boolean canDropOver(int current, int target) {
        if (!showAddItem())
            return true;
        int position = getAddItemPosition();
        return current != position && target != position;
    }

    public void addImage(Object image) {
        if (objectList.size() == maxSize) return;
        int oldCount = getItemCount();
        objectList.add(image);
        int newCount = getItemCount();
        int position = newCount - 1;
//        if (newCount == oldCount || newCount == maxSize)
//            notifyItemChanged(position);
//        else {
//            notifyItemInserted(position - 1);
//        }
        notifyDataSetChanged();
    }

    public void removeImage(int position) {
        if (position >= 0 && position < objectList.size()) {
            objectList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg, ivClear;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_img);
            ivClear = itemView.findViewById(R.id.iv_clear);
        }
    }
}
