package com.cahtegal.jadis.adapter;

/*
 * Created by faozi on 31/01/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cahtegal.jadis.R;
import com.cahtegal.jadis.model.ItemSlideMenu;

import java.util.List;

public class SlideMenuAdapter extends BaseAdapter {

    private Context context;
    private List<ItemSlideMenu> listItem;

    public SlideMenuAdapter(Context context, List<ItemSlideMenu> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder")
        View v = View.inflate(context, R.layout.list_slide, null);
        ImageView imgMenu = v.findViewById(R.id.imgMenu);
        TextView tTitle = v.findViewById(R.id.title);

        ItemSlideMenu item = listItem.get(position);
        imgMenu.setImageResource(item.getImgId());
        tTitle.setText(item.getTitle());

        return v;
    }
}
