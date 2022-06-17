package com.tangrun.app;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tangrun.kits.image.ImageGridView;
import com.tangrun.kits.image.ImageGridViewImageLoader;
import com.tangrun.kits.image.ImageGridViewManager;
import com.tangrun.kits.image.ImageGridViewOnClickListener;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PictureSelectionConfig.imageEngine = GlideEngine.createGlideEngine();
        PictureSelectionConfig.compressFileEngine = new CompressFileEngine() {
            @Override
            public void onStartCompress(Context context, ArrayList<Uri> arrayList, OnKeyValueResultCallbackListener call) {
                Luban.with(context).load(arrayList).ignoreBy(100)
                        .setRenameListener(new OnRenameListener() {
                            @Override
                            public String rename(String filePath) {
                                return null;
                            }
                        })
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                if (call != null) {
                                    call.onCallback(source, compressFile.getAbsolutePath());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (call != null) {
                                    call.onCallback(source, null);
                                }
                            }

                            @Override
                            public void onSuccess(String source, File compressFile) {

                            }

                            @Override
                            public void onError(String source, Throwable e) {

                            }
                        }).launch();
            }
        };
        ImageGridViewManager.registerGlideLoader();
        ImageGridViewManager.registerGlobalLoader(LocalMedia.class, new ImageGridViewImageLoader<LocalMedia>() {
            @Override
            public void onLoad(ImageView imageView, LocalMedia localMedia) {
                Glide.with(imageView)
                        .load(localMedia.getAvailablePath())
                        .into(imageView);
            }
        });
        ImageGridViewManager.setGlobalListener(new ImageGridViewOnClickListener() {
            @Override
            public void onAddImage(ImageGridView view, List<Object> selectedList, int currentSize, int maxSize) {
                List<LocalMedia> list = new ArrayList<>();
                for (Object o : selectedList) {
                    if (o instanceof LocalMedia)
                    list.add((LocalMedia) o);
                }

                PictureSelector.create(MainActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .setSelectedData(list)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> arrayList) {
                                view.setImages(new ArrayList<>(arrayList));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }

            @Override
            public void onPreviewImage(ImageGridView view, List<Object> imageList, int position) {
                ArrayList<LocalMedia> list = new ArrayList<>();
                for (Object o : imageList) {
                    if (o instanceof LocalMedia)
                        list.add((LocalMedia) o);
                }
                PictureSelector.create(MainActivity.this)
                        .openPreview()
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .startActivityPreview(position, true, list);
            }
        });
        setContentView(R.layout.activity_main);
        ImageGridView view = findViewById(R.id.gv_img);
        view.addImage(LocalMedia.generateLocalMedia("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF", "image/gif"));
        view.addImage(LocalMedia.generateLocalMedia("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF", "image/jpeg"));
    }
}
