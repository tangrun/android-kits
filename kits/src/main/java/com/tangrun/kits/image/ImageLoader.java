package com.tangrun.kits.image;

import android.widget.ImageView;

public interface ImageLoader<T> {
    void onLoad(ImageView imageView,T t);
}
