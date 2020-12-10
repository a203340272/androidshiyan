package com.example.oliver.mynote;//个人日记账户
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
public class Mydatabase extends SQLiteOpenHelper {
    public static final String NAME = "users";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private Context mContext;
    public Mydatabase(Context context) {
        super(context, "notes", null, 1);
        // TODO Auto-generated constructor stub
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists "+ NAME + "(" + USERNAME
                + " varchar primary key, "
                + PASSWORD + " varchar NOT NULL" + ")";
        db.execSQL(sql);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
}
