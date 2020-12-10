package com.example.oliver.mynote;
import android.content.Intent;
import androidx.fragment.app.Fragment;
//import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
public class Fragment2 extends Fragment {
    TextView textView;
    String name;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        final Bundle bundle = getArguments();
        name = bundle.getString("username");
        textView = (TextView)view.findViewById(R.id.myname);
        textView.setText(name);
        Button button = (Button)view.findViewById(R.id.resetpw);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ResetPasswordActivity.class);
                intent.putExtra("username",name);
                startActivity(intent);
            }
        });
        return view;
    }
}