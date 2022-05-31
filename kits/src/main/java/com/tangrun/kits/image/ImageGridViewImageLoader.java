package com.tangrun.kits.image;

import android.widget.ImageView;

public interface ImageGridViewImageLoader<T> {
    void onLoad(ImageView imageView,T t);
}
