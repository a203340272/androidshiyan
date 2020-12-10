package com.example.oliver.mynote;//登录
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
public class LoginActivity extends AppCompatActivity {
    Mydatabase mydb;
    SQLiteDatabase db;
    EditText name;
    EditText password;
    String Sname;
    String Spw;
    CheckBox repw;
    int isMemory ;
    SharedPreferences preferences = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText)findViewById(R.id.author);
        password = (EditText)findViewById(R.id.password);
        repw = (CheckBox)findViewById(R.id.checkbox1);
        preferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        isMemory = preferences.getInt("isMemory",0);
        if(isMemory == 1){
            name.setText(preferences.getString("name",""));
            password.setText(preferences.getString("password",""));
            repw.setChecked(true);
        }
    }
    public void memory(){
        if(repw.isChecked()){
            if(preferences == null) {
                preferences = getSharedPreferences("userinfo",Context.MODE_PRIVATE);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name",Sname);
            editor.putString("password",Spw);
            editor.putInt("isMemory",1);
            editor.commit();
        }
        else{
            if(preferences == null) {
                preferences = getSharedPreferences("userinfo",Context.MODE_PRIVATE);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("isMemory",0);
            editor.commit();
        }
    }
    public void registerOnclick(View view){   //点击注册按钮
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }
    public void loginOnclick(View view){     //点击登录按钮判断
        Sname = name.getText().toString();
        Spw  = password.getText().toString();
        mydb = new Mydatabase(this);
        db = mydb.getWritableDatabase();
        if(Sname.length()==0||Spw.length()==0)
            Toast.makeText(LoginActivity.this, "请输入完整信息！", Toast.LENGTH_LONG).show();
        else {
            Cursor c = db.rawQuery("select * from users where username = ? and password = ?", new String[]{Sname, Spw});
            if (c.getCount() == 0)
                Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
            else{
                memory();
                Intent i = new Intent(LoginActivity.this,UserActivity.class);
                i.putExtra("username",Sname);
                startActivity(i);
                db.close();
            }
        }
    }
}