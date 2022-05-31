package com.tangrun.kits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Random;

public class ActivityResultHelper extends Fragment {
    private static SparseArray<OnActivityResultCallback> sArray;

    public static void request(FragmentActivity activity, Intent intent, OnActivityResultCallback callback) {
        newInstance(intent).prepareRequest(activity, callback);
    }

    private static ActivityResultHelper newInstance(Intent intent) {
        if (sArray == null)
            sArray = new SparseArray<>();
        ActivityResultHelper fragment = new ActivityResultHelper();
        Bundle bundle = new Bundle();

        int requestCode;
        // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            // Studio编译的APK请求码必须小于65536
            // Eclipse编译的APK请求码必须小于256
            requestCode = new Random().nextInt(255);
        } while (sArray.get(requestCode) != null);

        bundle.putInt("code", requestCode);
        bundle.putParcelable("data", intent);
        fragment.setArguments(bundle);
        return fragment;
    }


    private void prepareRequest(FragmentActivity activity, OnActivityResultCallback callback) {
        int code = getArguments().getInt("code");
        sArray.put(code, callback);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(this, activity.getClass().getName())
                .commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OnActivityResultCallback callback = sArray.get(requestCode);
        if (callback != null) {
            callback.onActivityResult(resultCode, data);
        }
        sArray.remove(requestCode);
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
