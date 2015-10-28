package com.app2m.demo.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app2m.demo.MyApp;
import com.app2m.demo.R;

import java.lang.reflect.Field;

/**
 * Created by Cong Hao on 2015/5/11.
 * Email: hao.cong@qq.com
 */
public class DrawerLayoutLeftMenuAdapter extends BaseAdapter {
    private MyApp myApp;
    protected LayoutInflater mInflater;
    private String[] arrLeftMenuTitle;
    private String[] arrLeftMenuIcon;

    public DrawerLayoutLeftMenuAdapter(Context context) {
        if(context instanceof MyApp) {
            myApp = (MyApp)context;
        } else {
            myApp = (MyApp)context.getApplicationContext();
        }
        mInflater = (LayoutInflater) myApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrLeftMenuTitle = myApp.getResources().getStringArray(R.array.left_menu_text);
        arrLeftMenuIcon = myApp.getResources().getStringArray(R.array.left_menu_icon);
    }
    @Override
    public int getCount() {
        return arrLeftMenuTitle.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }
    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imgIcon;
        public TextView txtName;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.drawer_left_menu_item, parent, false);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.drawer_left_menu_item_icon);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.drawer_left_menu_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position >= this.arrLeftMenuIcon.length) return convertView;
        viewHolder.txtName.setText(this.arrLeftMenuTitle[position]);
        viewHolder.imgIcon.setImageResource(getImageByReflect(this.arrLeftMenuIcon[position]));
        return convertView;
    }
    private int getImageByReflect(String imageName){
        try {
            imageName = imageName.substring(imageName.lastIndexOf("/")+1, imageName.lastIndexOf("."));
            Field field = Class.forName(myApp.getApplicationContext().getPackageName()+".R$mipmap").getField(imageName);
            return field.getInt(field);
        } catch (Exception e) {
            return -1;
        }

    }
}
