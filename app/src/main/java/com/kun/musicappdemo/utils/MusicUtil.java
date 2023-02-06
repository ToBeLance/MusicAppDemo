package com.kun.musicappdemo.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kun.musicappdemo.R;
import com.kun.musicappdemo.bean.Song;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class MusicUtil {
    /**
     * song.duration --> 00.00 格式化
     */
    public static String getTime(int duration) {
        int min = duration/1000/60;
        int second = duration/1000 - (min*60);
        String time = "";
        if (min < 10) {
            time = "0" + min + ":";
        } else {
            time = time + min + ":";
        }
        if (second < 10) {
            time = time + "0" + second;
        } else {
            time = time + second;
        }
        return time;
    }
    /**
    获取外部存储的歌曲
     */
    public static ArrayList<Song> getAllSongs(Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        Song song;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC,null, MediaStore.Audio.Media.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext())
            {
                song = new Song();
                song.setSongID(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                song.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                song.setAlbumID(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setYear(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if (!Objects.equals(song.getArtist(), "<unknown>")) {
                    songs.add(song);
                }
            }
            cursor.close();
        }
        return songs;
    }
    /**
     * 从文件当中获取专辑封面位图
     */
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    public static Bitmap getArtworkFromFile(Context context, long songId, long albumId){
        Bitmap bm = null;
        if(albumId < 0 && songId < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumId < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songId + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // 只进行大小判断
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例，根据需要开启这一行代码
//            options.inSampleSize = 100;
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 展示具体可随手指滑动的歌曲播放详情页(BottomSheetDialog)
     * @param context  Activity的上下文
     * @param screenHeight 屏幕高度
     */
    //已弃用，不具扩展性
    public static void showSongPlayDetail(Context context,int screenHeight) {
        View view = View.inflate(context, R.layout.dialog_bottomsheet, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.TransparentDialogStyle);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        BottomSheetBehavior<View> mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//设置为展开状态
        mDialogBehavior.setPeekHeight(screenHeight);//设置默认高度，折叠态
        mDialogBehavior.setSkipCollapsed(true);//跳过折叠状态（展示、或下滑时不跳过，会停留在那）
        bottomSheetDialog.show();
    }

}
