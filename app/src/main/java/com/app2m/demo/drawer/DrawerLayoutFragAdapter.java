package com.app2m.demo.drawer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app2m.demo.MyApp;
import com.app2m.demo.R;
import com.app2m.demo.VolleySingleton;

import java.util.List;

/**
 * Created by Cong Hao on 2015/5/14.
 * Email: hao.cong@qq.com
 */
public class DrawerLayoutFragAdapter extends BaseAdapter {
    private MyApp myApp;
//    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    protected LayoutInflater mInflater;
    private List<String> dataList;
    public DrawerLayoutFragAdapter(Context context, List<String> dataList) {
        myApp = (MyApp)context.getApplicationContext();
//        if(context instanceof MyApp) {
//            myApp = (MyApp)context;
//        } else {
//            myApp = (MyApp)context.getApplicationContext();
//        }
//        mRequestQueue = Volley.newRequestQueue(context);
//        imageLoader = new ImageLoader(mRequestQueue, new BitmapCache(myApp));
        imageLoader = VolleySingleton.getInstance(myApp).getImageLoader();
        mInflater = (LayoutInflater) myApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder {
        public NetworkImageView img;
        public TextView txt;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_drawer_item, parent, false);
            viewHolder.img = (NetworkImageView) convertView.findViewById(R.id.swipy_item_img);
            viewHolder.img.setDefaultImageResId(R.mipmap.ic_launcher);
            viewHolder.img.setErrorImageResId(android.R.drawable.btn_star);
            viewHolder.txt = (TextView) convertView.findViewById(R.id.swipy_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String imgUrl = dataList.get(position);
        if (!TextUtils.isEmpty(imgUrl)) {
            viewHolder.img.setImageUrl(imgUrl, imageLoader);
            viewHolder.txt.setText("position: "+(position+1));
        }
        return convertView;
    }
    public void stop() {
//        if (mRequestQueue != null) {
//            mRequestQueue.ca.cancelAll();
//        }
    }

}
