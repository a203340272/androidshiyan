package com.example.servicebestpractice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
//import android.support.v4.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.servicebestpractice.db.Quku;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    //private final String webadress = "https://freemusicarchive.org/genre/Classical/";
    private List<Quku> qukuList;
    private ListView listView;
    private MyAdapter2 myAdapter;
    private DBmanage dBmanage;
    private Button backToFirst;
    private String responsetext;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    qukuList = dBmanage.getAllQuku();
                    myAdapter = new MyAdapter2(getApplicationContext(), qukuList);
                    listView.setAdapter(myAdapter);
                    break;
                default:
                    break;
            }
        }
    };
    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LitePal.getDatabase();
        dBmanage = new DBmanage();
        dBmanage.deleteAllQuku();//删除之前的曲库，因为有的歌曲下载链接会更新
        for(int i=1;i<=3;i++){
            String webadress="https://freemusicarchive.org/genre/Classical/?sort=track_date_published&d=1&page="+String.valueOf(i);
            queryFromServer(webadress);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backToFirst = (Button) findViewById(R.id.backToFirst);
        listView = (ListView) findViewById(R.id.list_view2);
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        startService(intent); // 启动服务
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = qukuList.get(position).getSongname();
                if (songname.indexOf("(") != -1) {
                    int begin = songname.indexOf("(")-1;
                    int end = songname.indexOf(")");
                    songname = songname.substring(0, begin) + songname.substring(end + 1, songname.length());
                }
                if (songname.indexOf(":") != -1) {
                    int begin = songname.indexOf(":");
                    songname = songname.substring(0, begin);
                }
                String url = qukuList.get(position).getSongURL() + "/" + songname + ".mp3";
                Log.d("chaoo", url);
                if (downloadBinder == null) {
                    return;
                }
                //if (downloadBinder.marsk == false) {
                downloadBinder.startDownload(url, qukuList.get(position).getSongname() + ".mp3");
                // } else if (downloadBinder.marsk == true) {
                //downloadBinder.pauseDownload();
                // }
            }
        });
        //listView的长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提醒")
                        .setMessage("你确定要取消下载么？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        downloadBinder.cancelDownload();
                                    }
                                }).show();

                return true;
            }
        });
        backToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FirstActivity.class));
                MainActivity.this.finish();
            }
        });
    }

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this,FirstActivity.class));
        MainActivity.this.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    /**
     * 自定义listview的适配器
     */
    class MyAdapter2 extends BaseAdapter {
        List<Quku> qukus;
        LayoutInflater inflater;

        public MyAdapter2(Context context, List<Quku> qukus) {
            this.qukus = qukus;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return qukus.size();
        }

        @Override
        public Object getItem(int arg0) {
            return qukus.get(arg0);
        } //得到数据项

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }//得到数据项下标

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder2 viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder2();
                convertView = inflater.inflate(R.layout.list_item, null);
                viewHolder.songname = (TextView) convertView.findViewById(R.id.list_songname);
                viewHolder.songauthor = (TextView) convertView.findViewById(R.id.list_songauthor);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder2) convertView.getTag();
            }
            viewHolder.songname.setText(qukus.get(position).getSongname());
            viewHolder.songauthor.setText(qukus.get(position).getAuthor());
            return convertView;
        }

        class ViewHolder2 {
            TextView songname;
            TextView songauthor;
        }
    }

    private void queryFromServer(String address) {
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responsetext = response.body().string();
                handleData(responsetext);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取歌曲信息失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });
    }

    //处理服务器返回的数据
    private void handleData(String content) {
        Document doc = Jsoup.parse(content);
        Elements items = doc.select("div.play-item.gcol.gid-electronic");
        for (Element item : items) {
            String author = item.select("div[class=playtxt]>span[class=ptxt-artist]").select("a").text();
            String songname = item.select("div[class=playtxt]>span[class=ptxt-track]").text();
            String songURL = item.select("span[class=playicn]").select("a[title=Download]").attr("href");
            if (dBmanage.noExistQuku(songname, songURL)) {
                dBmanage.addQuku(author, songname, songURL);
            }
        }
    }
}
