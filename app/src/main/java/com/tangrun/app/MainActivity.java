package com.tangrun.app;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tangrun.kits.adapter.FragmentAdapter;
import com.tangrun.kits.image.ImageGridView;
import com.tangrun.kits.page.PageHelper;
import com.tangrun.kits.widget.ScrollableViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        ImageGridView<LocalMedia> view = findViewById(R.id.gv_img);
        view.setOnImageLoadListener(new ImageGridView.OnImageLoadListener<LocalMedia>() {
            @Override
            public void onLoad(ImageView imageView, LocalMedia data) {
                Glide.with(imageView)
                        .load(data.getAvailablePath())
                        .apply(
                                RequestOptions
                                        .placeholderOf(R.drawable.kits_baseline_image_24)
                                        .error(R.drawable.kits_baseline_broken_image_24)
                        )
                        .into(imageView);
            }
        });
        view.setOnAddImageListener(new ImageGridView.OnAddImageListener<LocalMedia>() {
            @Override
            public void onAddImage(ImageGridView view, List<LocalMedia> selectedList) {
                PictureSelector.create(MainActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .setSelectedData(selectedList)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> arrayList) {
                                view.setList(new ArrayList<>(arrayList));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });
        view.setOnPreviewImageListener(new ImageGridView.OnPreviewImageListener<LocalMedia>() {
            @Override
            public void onPreviewImage(ImageGridView view, List<LocalMedia> imageList, int position) {
                PictureSelector.create(MainActivity.this)
                        .openPreview()
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .startActivityPreview(position, true, imageList instanceof ArrayList ? (ArrayList<LocalMedia>) imageList : new ArrayList<>(imageList));
            }
        });

        List<LocalMedia> localMedia = new ArrayList<>();
        localMedia.add(LocalMedia.generateLocalMedia("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF", "image/gif"));
        localMedia.add(LocalMedia.generateLocalMedia("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF", "image/jpeg"));
//        view.addAll(localMedia);

        {
            ScrollableViewPager viewPager = findViewById(R.id.vp_content);
            FragmentAdapter<Fragment> fragmentAdapter = new FragmentAdapter<>(getSupportFragmentManager());
            fragmentAdapter.setAutoMaxOffscreenPageLimit(true);
            fragmentAdapter.addFragment(new MainFragment());
            fragmentAdapter.addFragment(new MainFragment());
            fragmentAdapter.addFragment(new MainFragment());
            fragmentAdapter.addFragment(new MainFragment());
            viewPager.setAdapter(fragmentAdapter);
        }


        LinearLayout llRoot = findViewById(R.id.ll_root);
        {
            AppCompatButton button = new AppCompatButton(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            llRoot.addView(button);

            button.setText("插入位置1");
            button.setOnClickListener(v -> {
                PictureSelector.create(MainActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> arrayList) {
                                view.addAll(1,new ArrayList<>(arrayList));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            });
        }
        {
            AppCompatButton button = new AppCompatButton(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            llRoot.addView(button);

            button.setText("替换");
            button.setOnClickListener(v -> {
                PictureSelector.create(MainActivity.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> arrayList) {
                                view.setList(new ArrayList<>(arrayList));
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            });
        }
    }
}
