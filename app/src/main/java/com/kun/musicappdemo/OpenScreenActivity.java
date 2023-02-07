package com.kun.musicappdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.kun.musicappdemo.utils.GetSomePermissionUtil;

public class OpenScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_open_screen);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //这里实现了开屏动画，可以避免用户快速退出进入APP,再操作 MediaPlayer 导致的 MediaPlayer 状态异常
                startActivity(new Intent(OpenScreenActivity.this,MainActivity.class));
                overridePendingTransition(R.anim.in,R.anim.out);//添加 activity 跳转动画，使过渡更加丝滑
                OpenScreenActivity.this.finish();//将开屏 activity 销毁，防止返回键时，回到该界面
            }
        }).start();
    }
}