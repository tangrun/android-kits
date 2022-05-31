package com.tangrun.kits.image;

import java.util.List;

public interface ImageGridViewOnClickListener {
    void onAddImage(ImageGridView view, List<Object> selectedList, int currentSize, int maxSize);

    void onPreviewImage(ImageGridView view, List<Object> imageList, int position);
}
