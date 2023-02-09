package com.kun.musicappdemo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kun.musicappdemo.R;
import com.kun.musicappdemo.bean.Song;
import com.kun.musicappdemo.utils.MusicUtil;

//音乐详情播放页面
public class MyMusicPlayBottomSheetDialog extends BottomSheetDialog {

    public MyMusicPlayBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //将dialog扩展到状态栏
        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //添加入场动画，退出也可以设置，不过默认已经满足需求
        getWindow().setWindowAnimations(R.style.MyBottomSheetDialogAnimation);

    }
}
