package com.example.oliver.mynote;//注册
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.content.*;
import android.database.Cursor;
public class RegisterActivity extends AppCompatActivity{
    Mydatabase mydb;
    SQLiteDatabase db;
    String Sname;
    String Spw;
    String Srepw;
    EditText name;
    EditText pw;
    EditText repw;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void registerOnclick(View view){//点击注册
        name = (EditText)findViewById(R.id.author);
        Sname = name.getText().toString();
        pw = (EditText)findViewById(R.id.password);
        Spw = pw.getText().toString();
        repw = (EditText)findViewById(R.id.repassword);
        Srepw = repw.getText().toString();
        mydb = new Mydatabase(this);
        db = mydb.getWritableDatabase();
        if(Sname.length()==0||Spw.length()==0||Srepw.length()==0)
            Toast.makeText(RegisterActivity.this, "请输入完整信息！", Toast.LENGTH_LONG).show();
        else{
            if(!Spw.equals(Srepw))
                Toast.makeText(RegisterActivity.this, "密码不一致！", Toast.LENGTH_LONG).show();
            else{
                Cursor c = db.rawQuery("select * from users where username = ?",new String[]{Sname});
                if(c.getCount()!=0){
                    Toast.makeText(RegisterActivity.this, "用户名已存在！", Toast.LENGTH_LONG).show();
                }
                else{
                    ContentValues cv = new ContentValues();
                    cv.put("username",Sname);
                    cv.put("password",Spw);
                    db.insert("users",null,cv);
                    db.close();
                    Intent i = new Intent(RegisterActivity.this,RegistersucceedActivity.class);
                    startActivity(i);
                }
            }
        }
    }
}