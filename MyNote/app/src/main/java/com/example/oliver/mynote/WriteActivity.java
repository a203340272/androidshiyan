package com.example.oliver.mynote;//添加日记
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.view.*;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class WriteActivity extends AppCompatActivity {
    EditText editText1;
    EditText editText2;
    String title;
    String mainbody;
    Mydatabase2 mydb;
    SQLiteDatabase db;
    GridView gridview;
    Bitmap bmp;
    int id;
    String name;
    SimpleAdapter simpleAdapter;
    private final int IMAGE_CODE = 0;
    Uri bitmapUri = null;
    ArrayList<HashMap<String, Object>> imagelist;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_write);
        Intent intent = this.getIntent();
        name = intent.getStringExtra("username");
        editText1 = (EditText)findViewById(R.id.title);

        editText2 = (EditText)findViewById(R.id.mainbody);

        gridview = (GridView)findViewById(R.id.gridview);
        imagelist = new ArrayList<HashMap<String, Object>>();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                delect(position);
            }
        });
    }
    protected void onResume() {
        super.onResume();
        if(bmp!=null) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("images", bmp);
            imagelist.add(map);
            simpleAdapter = new SimpleAdapter(this, imagelist, R.layout.images, new String[]{"images"}, new int[]{R.id.img});
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
    }
    public void delect(final int position){         //删除照片
        AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imagelist.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void submitOnclick(View view){  //添加按钮
        Button button;
        button = (Button)view;
        button.setEnabled(false);
        mydb = new Mydatabase2(this);
        db = mydb.getWritableDatabase();
        title = editText1.getText().toString();
        mainbody = editText2.getText().toString();
        if(title.length()==0||mainbody.length()==0){
            Toast.makeText(WriteActivity.this, "请输入完整内容！", Toast.LENGTH_LONG).show();
            button.setEnabled(true);
        }
        else {
            ContentValues cv = new ContentValues();
            if (imagelist.size() != 0) {
                for(int i = 0;i<imagelist.size();i++) {
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bmp = (Bitmap)imagelist.get(i).get("images");
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                    cv.put("img"+String.valueOf(i+1), os.toByteArray());
                }
            }
            cv.put("username",name);
            cv.put("title",title);
            cv.put("mainbody",mainbody);
            db.insert("notepad","id",cv);
            db.close();
            Toast.makeText(WriteActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(WriteActivity.this,UserActivity.class);
            intent.putExtra("username",name);
            startActivity(intent);
            /*db.execSQL("insert into notepad values ( null,'" + name + "','" + title + "','" + mainbody + "')");*/
        }
    }
    public void resetOnclick(View view){   //重置按钮
        editText1.setText("");
        editText2.setText("");
        imagelist.clear();
        simpleAdapter.notifyDataSetChanged();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setimgOnclick(View view){
        if(imagelist.size()==4)
            Toast.makeText(WriteActivity.this, "最多只能添加四张图片！", Toast.LENGTH_LONG).show();
        else
            selectImage();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectImage() {        //打开相册
        // TODO Auto-generated method stub
        boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;   //解析封装过的uri
        Intent getAlbum;
        if (isKitKatO) {
            getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        }
        String IMAGE_TYPE = "image/*";
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, IMAGE_CODE);        //打开相册
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.e("TAG->onresult", "ActivityResult resultCode error");
            return;
        }
        ContentResolver resolver = getContentResolver();
        if (requestCode == IMAGE_CODE) {
            try {
                Uri originalUri = data.getData();  //获得图片的uri
                bitmapUri = originalUri;
                bmp = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }
        }
    }
}
