package com.tangrun.kits.image;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import androidx.annotation.NonNull;
import com.tangrun.kits.adapter.ListAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageGridViewAddAdapter extends ImageGridViewBaseAdapter<Void>{

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageGridViewViewHolder viewHolder, int position) {
        viewHolder.ivImg.setImageResource(imageGridView.getAddIcon());
        viewHolder.ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageGridView.isAddable()) {
                    ImageGridView.OnAddImageListener<Object> onAddImageListener = imageGridView.getOnAddImageListener();
                    if (onAddImageListener != null) {
                        onAddImageListener.onAddImage(imageGridView, imageGridView.getListAdapter().getDataList());
                    }
                }
            }
        });
        viewHolder.ivClear.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
