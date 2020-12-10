package com.example.servicebestpractice;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
//import android.support.v4.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.servicebestpractice.db.DBmanage;
import com.example.servicebestpractice.db.SongInfo;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private int index; //歌曲下标
    private List<SongInfo> songInfoList = new ArrayList<>();
    private DBmanage dBmanage;
    private TextView totaltime;   //总时长
    private TextView currenttime;//当前时间
    private SeekBar seekBar;//进度条
    private TextView songtitle;
    private TextView songauthor;
    private ImageView albumPhoto;
    private boolean isStop;//线程标志位
    private Button play;
    private Button pause;
    private Button previous;
    private Button next;
    private Button back;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 将SeekBar位置设置到当前播放位置
            seekBar.setProgress(msg.what);
            //获得音乐的当前播放时间
            currenttime.setText(formatime(msg.what));
            if(currenttime.getText().toString().equals(totaltime.getText().toString())){
                index = index + 1;
                if (index >= songInfoList.size()) {
                    index = 0;
                }
                initMediaPlayer();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        play = (Button) findViewById(R.id.play);//播放
        pause = (Button) findViewById(R.id.pause);//暂停
        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);
        songtitle = (TextView) findViewById(R.id.music_title);
        songauthor = (TextView) findViewById(R.id.music_author);
        totaltime = (TextView) findViewById(R.id.music_time);
        currenttime = (TextView) findViewById(R.id.current_time);
        albumPhoto = (ImageView) findViewById(R.id.albumPhoto);
        back = (Button) findViewById(R.id.back);
        seekBar = (SeekBar) findViewById(R.id.playSeekBar);
        dBmanage = new DBmanage();
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        //stop.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        songInfoList = dBmanage.getAllSongInfo(); //返回所有数据项
        //得到从第一个页面传来的值
        index = getIntent().getIntExtra("position", 0);
        mediaPlayer = new MediaPlayer();
        if (ContextCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            initMediaPlayer(); // 初始化MediaPlayer
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //b为true标识用户通过手动方式更改进度条
                if (b) {
                    //seekto方法是异步方法,seekto方法的参数是毫秒，而不是秒
                    mediaPlayer.seekTo(i);
                    mediaPlayer.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initMediaPlayer() {
        mediaPlayer.reset();
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
        SongInfo songInfo = songInfoList.get(index);
        songtitle.setText(songInfo.getSongName().replace(".mp3", ""));
        songauthor.setText("－" + songInfo.getSingerName() + "－");
        totaltime.setText(formatime(songInfo.getSongLength()));
        Bitmap bm = getArtworkFromFile(getApplicationContext(), songInfo.getSongId(), songInfo.getAlbumId());
        albumPhoto.setImageBitmap(bm);
        try {
            mediaPlayer.setDataSource(songInfo.getSongPath()); // 指定音频文件的路径
            mediaPlayer.prepare(); // 让MediaPlayer进入到准备状态
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        seekBar.setMax(songInfo.getSongLength());
        new Thread(new SeekBarThread()).start();
        // 设置seekbar的最大值
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start(); // 开始播放
                }
                play.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause(); // 暂停播放
                }
                pause.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
                break;
            case R.id.previous: //上一首
                index = index - 1;
                if (index == -1) {
                    index = songInfoList.size() - 1;
                }
                initMediaPlayer();
                break;
            case R.id.next:  //下一首
                index = index + 1;
                if (index >= songInfoList.size()) {
                    index = 0;
                }
                initMediaPlayer();
                // mediaPlayer.start();
                break;
            case R.id.back:
                startActivity(new Intent(SecondActivity.this, FirstActivity.class));
                SecondActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SecondActivity.this,FirstActivity.class));
        SecondActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
        isStop = true;
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
    }

    //时间转换
    private String formatime(long length) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String totalTime = formatter.format(length);
        return totalTime;
    }


    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Bitmap bm = null;
        // 专辑id和歌曲id小于0说明没有专辑、歌曲，并抛出异常
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException(
                    "Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException ex) {
        }
        //如果获取的bitmap为空，则返回一个默认的bitmap
        if (bm == null) {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.album);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bm = bitmapDrawable.getBitmap();
        }
        return Bitmap.createScaledBitmap(bm, 150, 150, true);
    }


    //建立一个子线程实现Runnable接口
    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (mediaPlayer != null && isStop == false) {
                // 将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                try {
                    // 每80毫秒更新一次位置
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
