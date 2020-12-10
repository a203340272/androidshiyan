package com.example.servicebestpractice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.support.v4.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.servicebestpractice.db.DBmanage;
import com.example.servicebestpractice.db.SongInfo;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FirstActivity extends AppCompatActivity {
    private DBmanage dBmanage;
    private MyAdapter adapter;
    private ListView listView;
    private Button download;
    private List<SongInfo> songInfoList = new ArrayList<>();
//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 2:
//                    songInfoList = dBmanage.getAllSongInfo();
//                    adapter = new MyAdapter(getApplicationContext(), songInfoList);
//                    listView.setAdapter(adapter);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        LitePal.getDatabase();
        dBmanage = new DBmanage();
        download = (Button) findViewById(R.id.download);
        listView = (ListView) findViewById(R.id.list_view);
        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FirstActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //SystemClock.sleep(3500);
        searchFile();
        songInfoList = dBmanage.getAllSongInfo();
        adapter = new MyAdapter(getApplicationContext(), songInfoList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                FirstActivity.this.finish();
            }
        });
        //监听listview的长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                new AlertDialog.Builder(FirstActivity.this)
                        .setTitle("提醒")
                        .setMessage("你确定要删除么？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dBmanage.deleteSongInfo(songInfoList.get(position).getId());
                                        File file = new File(songInfoList.get(position).getSongPath());
                                        file.delete();//删除音乐文
                                        FirstActivity.this.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                MediaStore.Audio.Media._ID + "=" + songInfoList.get(position).getSongId(),null);
                                        songInfoList = dBmanage.getAllSongInfo(); //从MediaStore媒体库表项中删除音乐信息
                                        //刷新页面
                                        adapter = new MyAdapter(getApplicationContext(), songInfoList);//适配数据项
                                        listView.setAdapter(adapter);
                                    }
                                }).show();

                return true;
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
                FirstActivity.this.finish();
            }
        });
    }

    //    private void searchFile() {
//        String songName = "";
//        String songPath = "";
//        String singerName="";
//        dBmanage.deleteAllSongInfo();
//        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
//        File dir = new File(directory);
//        File[] files = dir.listFiles();
//        for (File file : files) {
//
//            if (file.exists() && file.getName().matches("^.*\\.mp3$")) {//
//                songName = file.getName().replace(".mp3", "");
//                singerName = dBmanage.getSingerNameBySongName(songName);
//                if(songName.indexOf(":")!=-1){
//                    int begin=songName.indexOf(":");
//                    songName=songName.substring(0,begin);
//                    File newfile=new File(directory+"/"+songName+".mp3");
//                    file.renameTo(newfile);
//                }
//                songPath = file.getPath();
//                //if (dBmanage.existSongInfo(songName)) {
//                //} else {
//                dBmanage.addSongInfo(songName, songPath, singerName);
//                //}
//            }
//        }
//        if (songPath.equals("")) {
//            Toast.makeText(FirstActivity.this, "找不到文件", Toast.LENGTH_SHORT).show();
//        }
//    }
    //权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void searchFile() {
        //扫描刚下载的文件，添加到媒体库，极为重要
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.exists() && file.getName().matches("^.*\\.mp3$")) {//
                MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, null, null);
            }
        }
        //扫描媒体库
        Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //歌曲ID
                long songid = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //图片ID
                long albumid = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                //歌曲名
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //专辑名
                String songAlbum = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌手名
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲路径
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲长度
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //加入SongInfo数据库
                if(dBmanage.noExistSongInfo(name)) {
                    dBmanage.addSongInfo(songid, albumid, name, songAlbum, path, singer, duration);
                }
            }
        }
        cursor.close();
    }

    /**
     * 自定义listview的适配器
     */
    class MyAdapter extends BaseAdapter {
        List<SongInfo> songInfos;
        LayoutInflater inflater;

        public MyAdapter(Context context, List<SongInfo> songInfos) {
            this.songInfos = songInfos;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return songInfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return songInfos.get(arg0);
        } //得到数据项

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }//得到数据项下标

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item, null);
                viewHolder.songname = (TextView) convertView.findViewById(R.id.list_songname);
                viewHolder.songauthor = (TextView) convertView.findViewById(R.id.list_songauthor);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.songname.setText(" " + songInfos.get(position).getSongName().replace(".mp3", ""));
            viewHolder.songauthor.setText("•  " + songInfos.get(position).getSingerName() + "\n" + "•" + "《" + songInfos.get(position).getSongAlbum() + "》");
            return convertView;
        }

        class ViewHolder {
            TextView songname;
            TextView songauthor;
        }
    }
}
