package com.example.oliver.mynote; //个人日记内容
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
public class Mydatabase2 extends SQLiteOpenHelper {
    public static final String NAME = "notepad";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String TITLE = "title";
    public static final String MAINBODY = "mainbody";
    public static final String TIME = "time";
    public static final String IMAGE1 = "img1";
    public static final String IMAGE2 = "img2";
    public static final String IMAGE3 = "img3";
    public static final String IMAGE4= "img4";
    private Context mContext;
    public Mydatabase2(Context context) {
        super(context, "notes1", null, 1);
        // TODO Auto-generated constructor stub
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {        //写进数据库
        String sql = "create table if not exists "+ NAME + "("
                + ID +" integer primary key AUTOINCREMENT, "
                + USERNAME + " varchar NOT NULL, "
                + TITLE + " varchar NOT NULL, "
                + MAINBODY  + " varchar NOT NULL,"
                + TIME + " TIMESTAMP default (datetime('now', 'localtime')),"
                + IMAGE1 + " blob,"
                + IMAGE2 + " blob,"
                + IMAGE3 + " blob,"
                + IMAGE4 + " blob"
                + ")";
        db.execSQL(sql);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
}