package com.example.oliver.mynote;//修改
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ResetActivity extends AppCompatActivity {
    EditText titletext;
    EditText mainbodytext;
    GridView gridView;
    String title;
    String mainbody;
    String id;
    String name;
    Mydatabase2 mydb;
    SQLiteDatabase db;
    byte[] in;
    Bitmap bmp = null;
    ArrayList<HashMap<String, Object>> image_list;
    SimpleAdapter simpleAdapter;
    private final int IMAGE_CODE = 0;
    Uri bitmapUri = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_reset);
        titletext = (EditText) findViewById(R.id.title);
        mainbodytext = (EditText) findViewById(R.id.mainbody);
        gridView = (GridView) findViewById(R.id.gridview);
        image_list = new ArrayList<HashMap<String, Object>>();
        Intent intent = this.getIntent();
        title = intent.getStringExtra("title");
        mainbody = intent.getStringExtra("mainbody");
        id = intent.getStringExtra("id");
        titletext.setText(title);
        mainbodytext.setText(mainbody);
        mydb = new Mydatabase2(this);
        db = mydb.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("select * from notepad where id = ?", new String[]{id});
        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndex("username"));
            in = c.getBlob(c.getColumnIndex("img1"));
            if (in != null) {
                bmp = BitmapFactory.decodeByteArray(in, 0, in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images", bmp);
                image_list.add(map);

            }
            in = c.getBlob(c.getColumnIndex("img2"));
            if (in != null) {
                bmp = BitmapFactory.decodeByteArray(in, 0, in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images", bmp);
                image_list.add(map);
                bmp = null;
            }
            in = c.getBlob(c.getColumnIndex("img3"));
            if (in != null) {
                bmp = BitmapFactory.decodeByteArray(in, 0, in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images", bmp);
                image_list.add(map);
                bmp = null;
            }
            in = c.getBlob(c.getColumnIndex("img4"));
            if (in != null) {
                bmp = BitmapFactory.decodeByteArray(in, 0, in.length);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("images", bmp);
                image_list.add(map);
                bmp = null;
            }
        }
        simpleAdapter = new SimpleAdapter(this, image_list, R.layout.images, new String[]{"images"}, new int[]{R.id.img});
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
        gridView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                delect(position);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (bmp != null) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("images", bmp);
            image_list.add(map);
            simpleAdapter = new SimpleAdapter(this, image_list, R.layout.images, new String[]{"images"}, new int[]{R.id.img});
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
            gridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    public void delect(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                image_list.remove(position);
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

    public void submitOnclick(View view) {
        mydb = new Mydatabase2(this);
        db = mydb.getWritableDatabase();
        title = titletext.getText().toString();
        mainbody = mainbodytext.getText().toString();
        if (title.length() == 0 || mainbody.length() == 0) {
            Toast.makeText(ResetActivity.this, "请输入完整内容！", Toast.LENGTH_LONG).show();
        } else {
            ContentValues cv = new ContentValues();
            if (image_list.size() != 0) {
                for (int i = 0; i < image_list.size(); i++) {
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bmp = (Bitmap) image_list.get(i).get("images");
                    assert bmp != null;
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                    cv.put("img" + String.valueOf(i + 1), os.toByteArray());
                }
                if (image_list.size() < 4) {
                    for (int i = image_list.size(); i < 4; i++) {
                        cv.put("img" + String.valueOf(i + 1), (byte[]) null);
                    }
                }
            } else {
                cv.put("img1", (byte[]) null);
                cv.put("img2", (byte[]) null);
                cv.put("img3", (byte[]) null);
                cv.put("img4", (byte[]) null);
            }
            cv.put("title", title);
            cv.put("mainbody", mainbody);
            db.update("notepad", cv, "id = ?", new String[]{id});
            db.close();
            Toast.makeText(ResetActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ResetActivity.this, UserActivity.class);
            intent.putExtra("username", name);
            startActivity(intent);
        }
    }

    public void resetOnclick(View view) {
        titletext.setText("");
        mainbodytext.setText("");
        image_list.clear();
        simpleAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setimgOnclick(View view) {
        if (image_list.size() == 4)
            Toast.makeText(ResetActivity.this, "最多只能添加四张图片！", Toast.LENGTH_LONG).show();
        else
            selectImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectImage() {
        // TODO Auto-generated method stub
        boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent getAlbum;
        if (isKitKatO) {
            getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        }
        String IMAGE_TYPE = "image/*";
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, IMAGE_CODE);
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
