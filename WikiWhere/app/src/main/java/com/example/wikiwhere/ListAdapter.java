package com.example.wikiwhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    Context c;
    ArrayList<String> titles;
    ArrayList<String> titles2;

    public ListAdapter(Context c, ArrayList<String> titles, ArrayList<String> titles2) {
        this.c = c;
        this.titles = titles;
        this.titles2 = titles2;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int i) {
        return titles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.single_list_item,viewGroup,false);
        }

        final String s= (String) this.getItem(i);

        //ImageView img= (ImageView) view.findViewById(R.id.spacecraftImg);
        TextView nameTxt= (TextView) view.findViewById(R.id.title);
        TextView propTxt= (TextView) view.findViewById(R.id.description);

        nameTxt.setText(s);
        propTxt.setText(s);
        //img.setImageResource(s.getImage());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}