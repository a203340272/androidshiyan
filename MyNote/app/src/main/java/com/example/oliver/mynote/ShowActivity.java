package com.example.oliver.mynote;  //显示内容
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
public class ShowActivity extends AppCompatActivity {
    Mydatabase2 mydb;
    SQLiteDatabase db;
    TextView titleview;
    TextView timeview;
    TextView mynameview;/////////////////
    TextView mainbodyview;
    GridView gridview;
    String title;
    String time;
    String myname;
    String mainbody;
    byte[] in;
    Bitmap bmp;
    ArrayList<HashMap<String, Object>> image_list;
    SimpleAdapter simpleAdapter;
    String id;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        titleview = (TextView)findViewById(R.id.title);
        timeview = (TextView)findViewById(R.id.time);
        mynameview = (TextView)findViewById(R.id.myname);///////////////
        mainbodyview = (TextView)findViewById(R.id.mainbody);
        image_list = new ArrayList<HashMap<String, Object>>();
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");
        mydb = new Mydatabase2(this);
        db = mydb.getWritableDatabase();
        Cursor c = db.rawQuery("select * from notepad where id = ?",new String[]{id});
        while(c.moveToNext()){
            title = c.getString(c.getColumnIndex("title"));//获取标题
            time = c.getString(c.getColumnIndex("time"));//获取时间
            myname = c.getString(c.getColumnIndex("username"));//////////////////////////获取作者
            mainbody = c.getString(c.getColumnIndex("mainbody"));//获取内容
            in = c.getBlob(c.getColumnIndex("img1"));//获取图片
            if(in!=null){
                bmp = BitmapFactory.decodeByteArray(in,0,in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images",bmp);
                image_list.add(map);
            }
            in = c.getBlob(c.getColumnIndex("img2"));
            if(in!=null){
                bmp = BitmapFactory.decodeByteArray(in,0,in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images",bmp);
                image_list.add(map);
            }
            in = c.getBlob(c.getColumnIndex("img3"));
            if(in!=null){
                bmp = BitmapFactory.decodeByteArray(in,0,in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images",bmp);
                image_list.add(map);
            }
            in = c.getBlob(c.getColumnIndex("img4"));
            if(in!=null){
                bmp = BitmapFactory.decodeByteArray(in,0,in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images",bmp);
                image_list.add(map);
            }
        }
        titleview.setText("标题："+title);
        timeview.setText("时间："+time);
        mynameview.setText("作者："+myname);//////////////保存添加作者到显示窗口
        mainbodyview.setText("正文："+mainbody);
        gridview = (GridView)findViewById(R.id.gridview);
        simpleAdapter = new SimpleAdapter(this,image_list,R.layout.images,new String[]{"images"},new int[]{R.id.img});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridview.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }
    public void resetOnclick(View view){
        Intent intent = new Intent(ShowActivity.this,ResetActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("title",title);
        intent.putExtra("myname",id);////////////////////
        intent.putExtra("mainbody",mainbody);
        startActivity(intent);
    }
}
