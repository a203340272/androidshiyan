package com.example.oliver.mynote;//修改密码

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class ResetPasswordActivity extends AppCompatActivity {
    EditText editText1;
    EditText editText2;
    String oldpassword;
    String newpassword;
    Mydatabase mydb;
    SQLiteDatabase db;
    Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        editText1 = (EditText)findViewById(R.id.oldpassword);
        editText2 = (EditText)findViewById(R.id.newpassword);
        intent = this.getIntent();
    }
    public void submitOnclick(View view){
        oldpassword = editText1.getText().toString();
        newpassword = editText2.getText().toString();
        mydb = new Mydatabase(this);
        db = mydb.getWritableDatabase();
        String name = intent.getStringExtra("username");
        if(oldpassword.length()==0||newpassword.length()==0)
            Toast.makeText(ResetPasswordActivity.this, "请输入完整信息！", Toast.LENGTH_LONG).show();
        else{
            Cursor c = db.rawQuery("select * from users where username = ? and password = ?", new String[]{name, oldpassword});
            if(c.getCount() == 0)
                Toast.makeText(ResetPasswordActivity.this, "密码错误！", Toast.LENGTH_LONG).show();
            else {
                db.execSQL("update users set password = '" + newpassword + "' where username = '" + name + "'");
                Toast.makeText(ResetPasswordActivity.this, "修改密码成功！", Toast.LENGTH_LONG).show();
                db.close();
            }
        }
    }
}
