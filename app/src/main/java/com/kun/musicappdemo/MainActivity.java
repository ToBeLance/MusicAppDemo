package com.kun.musicappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kun.musicappdemo.bean.Song;
import com.kun.musicappdemo.utils.MusicNotificationUtil;
import com.kun.musicappdemo.utils.MusicUtil;
import com.kun.musicappdemo.view.MyMusicPlayBottomSheetDialog;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory{
    //播放列表页的相关参数
    private RecyclerView songsRv;
    private SongsRVAdapter songsRVAdapter;
    //播放列表页的列表 SongsRV 适配器
    private class SongsRVAdapter extends RecyclerView.Adapter<SongsRVAdapter.ViewHolder> {

        @NonNull
        @Override
        public SongsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.songs_rv_item,parent,false));
        }

        //该方法绑定item视图，有缓存刷新的策略
        @Override
        public void onBindViewHolder(@NonNull SongsRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            song = allSongs.get(position);
            holder.songPos.setText("" + (position + 1));
            holder.songName.setText(song.getTitle());
            holder.singer.setText(song.getArtist());

            //根据是否是当前播放位置，选择显示状态
            if (position != playingPos) {//未选中状态
                holder.playingMask.setVisibility(View.INVISIBLE);
                holder.songPos.setTextColor(Color.BLACK);
                holder.songName.setTextColor(Color.BLACK);
                holder.singer.setTextColor(Color.BLACK);
            } else {//选中状态
                holder.playingMask.setVisibility(View.VISIBLE);
                holder.songPos.setTextColor(getResources().getColor(R.color.green_playing));
                holder.songName.setTextColor(getResources().getColor(R.color.green_playing));
                holder.singer.setTextColor(getResources().getColor(R.color.green_playing));
            }
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playingPos == position) {
                        return;
                    }
                    playingPos = position;
                    //播放歌曲
                    play();

                }
            });
        }

        @Override
        public int getItemCount() {
            return allSongs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View rootView;
            private View playingMask;
            private TextView songPos,songName,singer;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                rootView = itemView;
                playingMask = itemView.findViewById(R.id.playing_mask);
                songPos = itemView.findViewById(R.id.song_pos);
                songName = itemView.findViewById(R.id.song_name);
                singer = itemView.findViewById(R.id.singer);
            }
        }
    }
    private ImageSwitcher songAlbumImgSwitcher;
    private BitmapDrawable bitmapDrawable;
    private ObjectAnimator objectAnimator;//管理songAlbum的旋转动画
    private TextView songName;
    private ImageView playControl;
    private ArrayList<Song> allSongs;
    private Song song;
    private RequestOptions options = new RequestOptions().circleCropTransform();//圆形图片的RequestOptions
    private Bitmap artworkFromFile;//读取内存图片的bitmap
    private boolean isPlaying = false;//是否歌曲在播放状态的标志
    private int playingPos = -1;//记录当前播放歌曲的位置

    //绑定音乐播放后台服务的相关参数
    private MusicService.MusicControl musicControl;//为服务器返回的交互Binder类
    private MyServiceConn conn;
    //用于实现连接服务，比较模板化，不需要详细知道内容
    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl=(MusicService.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }

    private MusicBroadcastReceiver musicBroadcastReceiver;

    //音乐播放详情页的相关参数
    private View dialogView;//需要设置到 BottomSheetDialog 的 View
    private BottomSheetDialog bottomSheetDialog;//弹出的BottomSheetDialog， 也就是播放详情页
    private BottomSheetBehavior<View> mDialogBehavior;//管理 bottomSheetDialog 的弹出状态
    private ImageView cancelPlayingPage;
    private TextView dialogSongNameAndSinger;
    private ImageSwitcher dialogAlbumImgSwitcher;
    private ObjectAnimator dialogObjectAnimator;//管理dialogAlbum的旋转动画状态
    private static SeekBar seekBar;//这里为了和定义Handler，里面进行修改UI,设置为static
    private static TextView playedTime;//这里为了和定义Handler，里面进行修改UI,设置为static
    private TextView allTime;
    private ImageView preSong,nextSong,dialogPlayControl;

    //定义Handler与Service交互，动态修改UI状态；static 为了 Service能够直接访问
    @SuppressLint("HandlerLeak")
    public static Handler handler=new Handler(){//创建消息处理器对象
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg){
            Bundle bundle=msg.getData();//获取从子线程发送过来的音乐播放进度
            //获取当前进度currentPosition
            int currentPosition=bundle.getInt("currentPosition");
            //对进度条进行设置
            seekBar.setProgress(currentPosition);
            playedTime.setText(MusicUtil.getTime(bundle.getInt("currentPosition")));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//状态栏字体颜色
        setContentView(R.layout.activity_main);

        conn=new MyServiceConn();//创建服务连接对象
        bindService(new Intent(this,MusicService.class),conn,BIND_AUTO_CREATE);//绑定服务

        allSongs = MusicUtil.getAllSongs(this);//获取所有手机外部存储的歌曲

        initPlayListPageView();//初始化播放列表页的UI

        initPlayDetailPageView();//初始化播放详情页的UI

        musicBroadcastReceiver = new MusicBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicNotificationUtil.SMALL_REMOTEVIEWS_CONTROL);
        intentFilter.addAction(MusicNotificationUtil.SMALL_REMOTEVIEWS_NEXT_SONG);
        intentFilter.addAction(MusicNotificationUtil.SMALL_REMOTEVIEWS_CANCEL);
        intentFilter.addAction(MusicNotificationUtil.BIG_REMOTEVIEWS_PRE_SONG);
        intentFilter.addAction(MusicNotificationUtil.BIG_REMOTEVIEWS_CONTROL);
        intentFilter.addAction(MusicNotificationUtil.BIG_REMOTEVIEWS_NEXT_SONG);
        intentFilter.addAction(MusicNotificationUtil.BIG_REMOTEVIEWS_CANCEL);
        registerReceiver(musicBroadcastReceiver,intentFilter);

        MusicNotificationUtil.showNotification(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicControl.pausePlay();//音乐暂停播放
        unbindService(conn);//解绑服务
        unregisterReceiver(musicBroadcastReceiver);
        handler = null;
    }

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);//设置保持纵横比居中缩放图像
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                ImageSwitcher.LayoutParams.MATCH_PARENT, ImageSwitcher.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    private void initPlayListPageView() {
        songsRv = findViewById(R.id.songs_rv);
        songsRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));//添加Android自带的分割线
        songsRv.setLayoutManager(new LinearLayoutManager(this));
        songsRVAdapter = new SongsRVAdapter();
        songsRv.setAdapter(songsRVAdapter);
        songAlbumImgSwitcher = findViewById(R.id.song_album_imgSwitcher);
        songAlbumImgSwitcher.setFactory(this);
        songAlbumImgSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.in));
        songAlbumImgSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.out));
        songAlbumImgSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入App时无播放歌曲，不展示播放详情页
                if (songName.getText().toString().equals("no song")) {
                    Toast.makeText(MainActivity.this,"no song selected",Toast.LENGTH_SHORT).show();
                    return;
                }
                //在show之前， mDialogBehavior，管理 bottomSheetDialog 弹出的效果
                mDialogBehavior.setPeekHeight(ViewGroup.LayoutParams.MATCH_PARENT);//设置默认高度，折叠态
                mDialogBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//设置为展开状态
                mDialogBehavior.setSkipCollapsed(true);//跳过折叠态
                bottomSheetDialog.show();
            }
        });
        songName = findViewById(R.id.song_name);
        playControl = findViewById(R.id.play_control);
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlPlay();
            }
        });
        //加载一个无选择音乐的 songAlbum 图， 不至于太突兀
        songAlbumImgSwitcher.setImageResource(R.drawable.no_album_default_icon);
        //objectAnimator 管理 songAlbum 的旋转状态的初始化设置
        objectAnimator= ObjectAnimator.ofFloat(songAlbumImgSwitcher, "rotation",0,360);
        objectAnimator.setDuration(10000);
        objectAnimator.setInterpolator(new LinearInterpolator());//设置线性插值器，即匀速旋转
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);//设置旋转重复次数未一直循环
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);//设置旋转结束后，下次的旋转方向和之前的一致
        //把它开启了，然后马上停止，后面就不用重新开，保留状态就好
        objectAnimator.start();
        objectAnimator.pause();
    }

    private void initPlayDetailPageView() {
        dialogView = View.inflate(MainActivity.this, R.layout.dialog_bottomsheet, null);
        cancelPlayingPage = dialogView.findViewById(R.id.cancel_playing_page);
        cancelPlayingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
        dialogAlbumImgSwitcher = dialogView.findViewById(R.id.dialog_album_imgSwitcher);
        //...真的麻了，忘记初始化了
        dialogAlbumImgSwitcher.setFactory(this);
        dialogAlbumImgSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.in));
        dialogAlbumImgSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.out));
        dialogSongNameAndSinger = dialogView.findViewById(R.id.song_name_and_singer);
        preSong = dialogView.findViewById(R.id.pre_song);
        preSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preSong();

            }
        });
        nextSong = dialogView.findViewById(R.id.next_song);
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();

            }
        });
        dialogPlayControl = dialogView.findViewById(R.id.dialog_play_control);
        dialogPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlPlay();
            }
        });
        seekBar = dialogView.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //切换下一首
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicControl.seekTo(seekBar.getProgress());
            }
        });
        playedTime = dialogView.findViewById(R.id.played_time);
        allTime = dialogView.findViewById(R.id.all_time);
        bottomSheetDialog = new MyMusicPlayBottomSheetDialog(MainActivity.this,R.style.TransparentDialogStyle);
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.setDismissWithAnimation(true);//设置 bottomSheetDialog 具备进场退场动画
        mDialogBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        // ...dialogAlbumImgSwitcher
        dialogObjectAnimator= ObjectAnimator.ofFloat(dialogAlbumImgSwitcher, "rotation",0,360);
        //...不解
        dialogObjectAnimator.setDuration(10000);
        dialogObjectAnimator.setInterpolator(new LinearInterpolator());
        dialogObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        dialogObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        //把它开启了，然后马上停止，后面就不用重新开，保留状态就好
        dialogObjectAnimator.start();
        dialogObjectAnimator.pause();
    }

    private void preSong() {
        //当前播放歌曲为第一个位置，防止越界，不做执行，提示用户无上一首歌
        if (playingPos <= 0) {
            Toast.makeText(MainActivity.this,"no pre",Toast.LENGTH_SHORT).show();
            return;
        }
        playingPos--;
        //开始播放
        play();
    }

    private void nextSong() {
        //当前播放歌曲为最后一个位置，防止越界，不做执行，提示用户无下一首歌
        if (playingPos >= allSongs.size() - 1) {
            Toast.makeText(MainActivity.this,"no next",Toast.LENGTH_SHORT).show();
            return;
        }
        playingPos++;
        //开始播放
        play();
    }

    private void controlPlay() {
        //app, 刚进入无歌曲时，点击无反应，提示用户，点歌
        if (songName.getText().toString().equals("no song")) {
            Toast.makeText(MainActivity.this,"no song selected",Toast.LENGTH_SHORT).show();
            return;
        }
        if (isPlaying) {
            //播放暂停
            pausePlay();
        } else {
            //播放继续
            continuePlay();
        }
    }



    private void play() {

        MusicNotificationUtil.showNotification(MainActivity.this);

        song = allSongs.get(playingPos);//更新歌曲
        musicControl.play(song.getUrl());//调用 Service 播放歌曲

        //同步UI
        playControl.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        dialogPlayControl.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        MusicNotificationUtil.smallRemoteViews.setImageViewResource(R.id.play_control,R.drawable.ic_baseline_pause_circle_outline_24);
        MusicNotificationUtil.bigRemoteViews.setImageViewResource(R.id.play_control,R.drawable.ic_baseline_pause_circle_outline_24);

        artworkFromFile = MusicUtil.getArtworkFromFile(MainActivity.this,song.getSongID(),song.getAlbumID());
        //new BitmapDrawable 图片才不会缩小
        if (artworkFromFile != null) {
            bitmapDrawable = new BitmapDrawable(getResources(), artworkFromFile);
            songAlbumImgSwitcher.setImageDrawable(bitmapDrawable);
            dialogAlbumImgSwitcher.setImageDrawable(bitmapDrawable);
            MusicNotificationUtil.smallRemoteViews.setImageViewBitmap(R.id.album,artworkFromFile);
            MusicNotificationUtil.bigRemoteViews.setImageViewBitmap(R.id.album,artworkFromFile);
        } else {
            songAlbumImgSwitcher.setImageResource(R.drawable.no_album_default_icon);
            dialogAlbumImgSwitcher.setImageResource(R.drawable.no_album_default_icon);
            MusicNotificationUtil.smallRemoteViews.setImageViewResource(R.id.album,R.drawable.no_album_default_icon);
            MusicNotificationUtil.bigRemoteViews.setImageViewResource(R.id.album,R.drawable.no_album_default_icon);
        }


        MusicNotificationUtil.smallRemoteViews.setTextViewText(R.id.song_name,song.getTitle());
        MusicNotificationUtil.smallRemoteViews.setTextViewText(R.id.singer,song.getArtist());
        MusicNotificationUtil.bigRemoteViews.setTextViewText(R.id.song_name,song.getTitle());
        MusicNotificationUtil.bigRemoteViews.setTextViewText(R.id.singer,song.getArtist());
        MusicNotificationUtil.notifyNotification();

        songName.setText(song.getTitle());
        dialogSongNameAndSinger.setText(song.getTitle() + "-" + song.getArtist());
        allTime.setText(MusicUtil.getTime(song.getDuration()));
        seekBar.setMax(song.getDuration());
        songsRVAdapter.notifyDataSetChanged();
        if (objectAnimator.isPaused())objectAnimator.resume();
        if (dialogObjectAnimator.isPaused())dialogObjectAnimator.resume();

    }

    private void pausePlay() {
        //同步暂停播放时的UI状态
        objectAnimator.pause();
        dialogObjectAnimator.pause();
        playControl.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        dialogPlayControl.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        MusicNotificationUtil.smallRemoteViews.setImageViewResource(R.id.play_control,R.drawable.ic_baseline_play_circle_outline_24);
        MusicNotificationUtil.bigRemoteViews.setImageViewResource(R.id.play_control,R.drawable.ic_baseline_play_circle_outline_24);
        MusicNotificationUtil.notifyNotification();
        //调用Service的暂停播放方法
        musicControl.pausePlay();
        isPlaying = false;
    }

    private void continuePlay() {dialogObjectAnimator.resume();
        //同步继续播放时的UI状态
        objectAnimator.resume();
        dialogObjectAnimator.resume();
        playControl.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        dialogPlayControl.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        MusicNotificationUtil.smallRemoteViews.setImageViewResource(R.id.play_control,R.drawable.ic_baseline_pause_circle_outline_24);
        MusicNotificationUtil.bigRemoteViews.setImageViewResource(R.id.play_control,R.drawable.ic_baseline_pause_circle_outline_24);
        MusicNotificationUtil.notifyNotification();
        //调用Service的继续播放方法
        musicControl.continuePlay();
        isPlaying = true;
    }

    class MusicBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.SMALL_REMOTEVIEWS_CONTROL)) {
                controlPlay();
                Toast.makeText(MainActivity.this,"test SMALL_REMOTEVIEWS_CONTROL",Toast.LENGTH_SHORT).show();
            }
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.SMALL_REMOTEVIEWS_NEXT_SONG)) {
                nextSong();
                Toast.makeText(MainActivity.this,"test SMALL_REMOTEVIEWS_NEXT_SONG",Toast.LENGTH_SHORT).show();
            }
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.SMALL_REMOTEVIEWS_CANCEL)) {
                MusicNotificationUtil.cancelNotification();
                Toast.makeText(MainActivity.this,"test SMALL_REMOTEVIEWS_CANCEL",Toast.LENGTH_SHORT).show();
            }
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.BIG_REMOTEVIEWS_PRE_SONG)) {
                preSong();
                Toast.makeText(MainActivity.this,"test BIG_REMOTEVIEWS_PRE_SONG",Toast.LENGTH_SHORT).show();
            }
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.BIG_REMOTEVIEWS_CONTROL)) {
                controlPlay();
                Toast.makeText(MainActivity.this,"test BIG_REMOTEVIEWS_CONTROL",Toast.LENGTH_SHORT).show();
            }
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.BIG_REMOTEVIEWS_NEXT_SONG)) {
                nextSong();
                Toast.makeText(MainActivity.this,"test BIG_REMOTEVIEWS_NEXT_SONG",Toast.LENGTH_SHORT).show();
            }
            if (Objects.equals(intent.getAction(), MusicNotificationUtil.BIG_REMOTEVIEWS_CANCEL)) {
                MusicNotificationUtil.cancelNotification();
                Toast.makeText(MainActivity.this,"test BIG_REMOTEVIEWS_CANCEL",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

