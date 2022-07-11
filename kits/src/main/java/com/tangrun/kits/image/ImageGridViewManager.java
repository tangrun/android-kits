package com.tangrun.kits.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tangrun.kits.R;

import java.io.File;
import java.net.URL;

public class ImageGridViewManager {

    public static  <T> void registerGlobalLoader(Class<T> cls, ImageLoader<T> loader){
        ImageGridViewAdapter.globalLoaderMap.put(cls, loader);
    }

    private static void internalRegisterGlobalLoader(Class<?> cls, ImageLoader<?> loader){
        ImageGridViewAdapter.globalLoaderMap.put(cls, loader);
    }

    public static void setGlobalListener(ImageGridViewOnClickListener clickListener){
        ImageGridViewAdapter.globalOnClickListener = clickListener;
    }

    public static void registerGlideLoader(){
        ImageLoader<Object> loader = new ImageLoader<Object>() {
            @Override
            public void onLoad(ImageView imageView, Object o) {
                Glide.with(imageView)
                        .load(o)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.kits_baseline_image_24)
                                .error(R.drawable.kits_baseline_broken_image_24)
                        )
                        .into(imageView);
            }
        };
        internalRegisterGlobalLoader(Uri.class, loader);
        internalRegisterGlobalLoader(File.class, loader);
        internalRegisterGlobalLoader(byte[].class, loader);
        internalRegisterGlobalLoader(Bitmap.class, loader);
        internalRegisterGlobalLoader(String.class, loader);
        internalRegisterGlobalLoader(Drawable.class, loader);
        internalRegisterGlobalLoader(Integer.class, loader);
        internalRegisterGlobalLoader(URL.class, loader);
    }

}
