package com.tangrun.kits.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.net.URL;

public class ImageGridViewManager {

    public static  <T> void registerGlobalLoader(Class<T> cls,ImageGridViewImageLoader<T> loader){
        ImageGridViewAdapter.globalLoaderMap.put(cls, loader);
    }

    private static void internalRegisterGlobalLoader(Class<?> cls,ImageGridViewImageLoader<?> loader){
        ImageGridViewAdapter.globalLoaderMap.put(cls, loader);
    }

    public static void setGlobalListener(ImageGridViewOnClickListener clickListener){
        ImageGridViewAdapter.globalOnClickListener = clickListener;
    }

    public static void registerGlideLoader(){
        ImageGridViewImageLoader<Object> loader = new ImageGridViewImageLoader<Object>() {
            @Override
            public void onLoad(ImageView imageView, Object o) {
                Glide.with(imageView)
                        .load(o)
                        .into(imageView);
            }
        };
        internalRegisterGlobalLoader(Uri.class, loader);
        internalRegisterGlobalLoader(File.class, loader);
        internalRegisterGlobalLoader(byte[].class, loader);
        internalRegisterGlobalLoader(Bitmap.class, loader);
        internalRegisterGlobalLoader(String.class, loader);
        internalRegisterGlobalLoader(Drawable.class, new ImageGridViewImageLoader<Drawable>() {
            @Override
            public void onLoad(ImageView imageView, Drawable drawable) {
                imageView.setImageDrawable(drawable);
            }
        });
        internalRegisterGlobalLoader(Integer.class, new ImageGridViewImageLoader<Integer>() {
            @Override
            public void onLoad(ImageView imageView, Integer integer) {
                Glide.with(imageView)
                        .load(integer)
                        .into(imageView);
            }
        });
        internalRegisterGlobalLoader(URL.class, loader);
    }

}
