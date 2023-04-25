package com.tangrun.kits.image;

import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.tangrun.kits.R;

/**
 * @author RainTang
 */
class ImageGridViewViewHolder extends RecyclerView.ViewHolder {
    public final ImageView ivImg, ivClear;

    public ImageGridViewViewHolder(View itemView) {
        super(itemView);
        ivImg = itemView.findViewById(R.id.iv_img);
        ivClear = itemView.findViewById(R.id.iv_clear);
    }
}