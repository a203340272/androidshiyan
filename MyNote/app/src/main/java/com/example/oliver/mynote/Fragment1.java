package com.example.oliver.mynote;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
public class Fragment1 extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        String title;
        int n;
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> idlist = new  ArrayList<String>();
        final Bundle bundle = getArguments();
        Mydatabase2 mydb;
        final SQLiteDatabase db;
        mydb = new Mydatabase2(getActivity());
        db = mydb.getWritableDatabase();
        Cursor c = db.rawQuery("select * from notepad where username = ?",new String[]{bundle.getString("username")});
        while(c.moveToNext()){
            title = c.getString(c.getColumnIndex("title"));
            list.add(title);
            n = c.getInt(c.getColumnIndex("id"));
            idlist.add(String.valueOf(n));
        }
        c.close();
        final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),R.layout.activity_listview,list);
        final ListView listview = (ListView) view.findViewById(R.id.list_view);
        listview.setAdapter(adapter);
        Button button = (Button)view.findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),WriteActivity.class);
                intent.putExtra("username",bundle.getString("username"));
                startActivity(intent);
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String id = idlist.get(arg2);
                Intent intent = new Intent(getActivity(),ShowActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {               //长按删除日记
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确认删除该日记吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.execSQL("delete from notepad where id = "+ idlist.get(position));
                        list.remove(position);
                        idlist.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
        return view;
    }
}
