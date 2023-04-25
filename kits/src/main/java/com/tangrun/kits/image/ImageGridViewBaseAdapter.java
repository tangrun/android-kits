package com.tangrun.kits.image;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.tangrun.kits.R;
import com.tangrun.kits.adapter.ListAdapter;
import org.jetbrains.annotations.NotNull;

abstract class ImageGridViewBaseAdapter<D> extends ListAdapter<D,ImageGridViewViewHolder> {

    protected ImageGridView imageGridView;
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        imageGridView = (ImageGridView) recyclerView;
    }

    @NonNull
    @NotNull
    @Override
    public ImageGridViewViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ImageGridViewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.kits_item_image_grid_view, parent, false));
    }
}
