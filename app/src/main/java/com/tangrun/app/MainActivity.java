package com.tangrun.app;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.*;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tangrun.kits.image.ImageGridView;
import com.tangrun.kits.image.ImageLoader;
import com.tangrun.kits.image.ImageGridViewManager;
import com.tangrun.kits.image.ImageGridViewOnClickListener;
import com.tangrun.kits.recyclerview.DividerItemDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PictureSelectionConfig.imageEngine = GlideEngine.createGlideEngine();
        PictureSelectionConfig.compressFileEngine = new CompressFileEngine() {
            @Override
            public void onStartCompress(Context context, ArrayList<Uri> arrayList, OnKeyValueResultCallbackListener call) {
//                Luban.with(context).load(arrayList).ignoreBy(100)
//                        .setCompressListener(new OnCompressListener() {
//                            @Override
//                            public void onStart() {
//
//                            }
//
//                            @Override
//                            public void onSuccess(File file) {
//                                if (call != null) {
//                                    call.onCallback(source, compressFile.getAbsolutePath());
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                if (call != null) {
//                                    call.onCallback(source, null);
//                                }
//                            }
//
//                            @Override
//                            public void onSuccess(String source, File compressFile) {
//
//                            }
//
//                            @Override
//                            public void onError(String source, Throwable e) {
//
//                            }
//                        }).launch();
            }
        };
        ImageGridViewManager.registerGlideLoader();
        ImageGridViewManager.registerGlobalLoader(LocalMedia.class, new ImageLoader<LocalMedia>() {
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


        {
            RecyclerView recyclerView = findViewById(R.id.rv_divider);
            AtomicInteger atomicInteger = new AtomicInteger(200);
            findViewById(R.id.bt_notify)
                    .setOnClickListener(v -> {
                        atomicInteger.decrementAndGet();
                        recyclerView.getAdapter().notifyItemRemoved(0);
//                        recyclerView.getAdapter().notifyDataSetChanged();
                    });

            recyclerView.addItemDecoration(new DividerItemDecoration(this)
                    .setShowDivider(RecyclerView.HORIZONTAL, DividerItemDecoration.DIVIDER_ALL)
                    .setShowDivider(RecyclerView.VERTICAL, DividerItemDecoration.DIVIDER_ALL)
                    .setDividerDrawableSize(RecyclerView.HORIZONTAL, DividerItemDecoration.DIVIDER_MIDDLE, 16)
                    .setDividerDrawableSize(RecyclerView.HORIZONTAL, DividerItemDecoration.DIVIDER_BEGIN | DividerItemDecoration.DIVIDER_END, 64)
                    .setDividerDrawableSize(RecyclerView.VERTICAL, DividerItemDecoration.DIVIDER_ALL, 32)
                    .setDividerDrawableSize(RecyclerView.VERTICAL, DividerItemDecoration.DIVIDER_BEGIN | DividerItemDecoration.DIVIDER_END, 128)
            );
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//            GridLayoutManager layoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(new RecyclerView.Adapter() {

                @Override
                public int getItemViewType(int position) {
                    return position;
                }

                @NonNull
                @NotNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                    LinearLayout linearLayout = new LinearLayout(parent.getContext());
                    linearLayout.setBackgroundColor(Color.GRAY);
                    ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayout.setLayoutParams(layoutParams1);
                    TextView textView = new TextView(parent.getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, new Random().nextInt(200), getResources().getDisplayMetrics());
                    if (viewType <= 3){
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                    }else {
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                    }
                    if (viewType % 3 == 1){
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
                    }

                    if (viewType == 0){
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1800, getResources().getDisplayMetrics());
                    }
                    if (viewType == 1){
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                    }
                    if (viewType == 2){
                        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                    }

                    textView.setLayoutParams(layoutParams);
                    textView.setBackgroundColor(Color.RED);
                    linearLayout.addView(textView);
                    return new RecyclerView.ViewHolder(linearLayout) {
                    };
                }

                @Override
                public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
                    Log.d(TAG, "onBindViewHolder: " + position);
                    if (holder.itemView instanceof ViewGroup) {
                        View view = ((ViewGroup) holder.itemView).getChildAt(0);
                        if (view instanceof TextView) {
                            ((TextView) view).setText(position + " vvvvvvvvvvvvv");
                        }
                    }
                }

                @Override
                public int getItemCount() {
                    return 3;
//                    return atomicInteger.get();
                }
            });
        }
    }
}
