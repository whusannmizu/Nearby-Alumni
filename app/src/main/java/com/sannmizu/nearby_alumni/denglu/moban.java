package com.sannmizu.nearby_alumni.denglu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sannmizu.nearby_alumni.R;


public class moban extends LinearLayout {
    private ImageView lefticon,righticon;
    private TextView textcontext;
    public moban(Context context){
        super(context);
    }
    public moban(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }
    public moban init(){
        LayoutInflater.from(getContext()).inflate(R.layout.moban, this, true);
        lefticon=findViewById(R.id.left_icon);
        textcontext=findViewById(R.id.text_context);
        righticon=findViewById(R.id.right_icon);
        return this;
    }
    public moban initmine(int iconRes, String textContent){
        init();
        setlefticon(iconRes);
        settext(textContent);
        return this;
    }
    public moban setlefticon(int iconRes){
        lefticon.setImageResource(iconRes);
        return this;
    }
    public moban settext(String textContext){
        textcontext.setText(textContext);
        return this;
    }
}
