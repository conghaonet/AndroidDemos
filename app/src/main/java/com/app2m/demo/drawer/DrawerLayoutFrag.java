package com.app2m.demo.drawer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app2m.demo.MyApp;
import com.app2m.demo.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cong Hao on 2015/5/12.
 * Email: hao.cong@qq.com
 */
public class DrawerLayoutFrag extends Fragment {
    private static final String TAG = DrawerLayoutFrag.class.getName();
    private DrawerLayoutActivityCallBack mCallBack;
//    private SwipeRefreshLayout mSwipeLayout;
    private SwipyRefreshLayout mSwipeLayout;
    private int position;
    private List<String> listUrls;
    private List<String> dataList;
    private DrawerLayoutFragAdapter mDrawerLayoutFragAdapter;
    private MyHandler mHandler;
    private int intSelectionOfLoadMoreData;
    private ListView listView;

    protected void setPosition(int position) {
        this.position = position;
    }
    /**
     * 在Activity.onCreate方法之前调用，可以获取除了View之外的资源
     */
    @Override
    public void onInflate(Activity activity, AttributeSet attrs,Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        Log.d(TAG, TAG + "--onInflate");
    }

    /**
     * 当fragment第一次与Activity产生关联时就会调用，以后不再调用
     * 当该Fragment被添加,显示到Activity时调用该方法
     * 在此判断显示到的Activity是否已经实现了接口
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, TAG + "--onAttach");
        if (!(activity instanceof DrawerLayoutActivityCallBack)) {
            throw new IllegalStateException(TAG + " 所在的Activity必须实现接口：" + DrawerLayoutActivityCallBack.class.getName());
        }
        mCallBack = (DrawerLayoutActivityCallBack)activity;
        listUrls = new ArrayList<String>();
        Collections.addAll(listUrls, MyApp.ARRAY_IMAGE_URL);
    }

    /**
     * 在onAttach执行完后会立刻调用此方法，通常被用于读取保存的状态值，
     * 获取或者初始化一些数据，但是该方法不执行，窗口是不会显示的，因此如果获取的数据需要访问网络，最好新开线程。
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, TAG + "--onCreate");
    }

    /**
     * 创建Fragment中显示的view, 其中inflater用来装载布局文件，
     * container表示<fragment>标签的父标签对应的ViewGroup对象，savedInstanceState可以获取Fragment保存的状态
     * @param inflater 用来装载布局文件
     * @param container 表示 <fragment> 标签的父标签对应的 ViewGroup 对象
     * @param savedInstanceState 可以获取 Fragment 保存的状态
     * @return 返回Fragment中显示的view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, TAG + "--onCreateView");
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    /**
     * 继上面的onCreateView执行完后，就会调用此方法
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, TAG + "--onViewCreated");
        mHandler = new MyHandler(this);
//        mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.backlog_approval_swipe);
//        mSwipeLayout.setOnRefreshListener(new MyOnRefreshListener());
        mSwipeLayout = (SwipyRefreshLayout)view.findViewById(R.id.swipy_layout);
        mSwipeLayout.setOnRefreshListener(new MyOnRefreshListener());

        //设置刷新时动画的颜色，可以设置4个
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        TextView txtTitle = (TextView)view.findViewById(R.id.fragment_drawer_title);
        txtTitle.setText(txtTitle.getText() + "" + (position+1));
        listView = (ListView)view.findViewById(R.id.swipy_layout_listview);
        if(position == 0) {
            txtTitle.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            mSwipeLayout.setVisibility(View.VISIBLE);
            dataList = new ArrayList<String>();
            for(int i=0;i<20;i++) {
                if(listUrls.isEmpty()) break;
                dataList.add(listUrls.remove(0));
            }
            mDrawerLayoutFragAdapter = new DrawerLayoutFragAdapter(mCallBack.getMyApp(), dataList);
            listView.setAdapter(mDrawerLayoutFragAdapter);
        } else {
            txtTitle.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            mSwipeLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 在Activity.onCreate方法调用后会立刻调用此方法，表示窗口已经初始化完毕，此时可以调用控件了
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, TAG + "--onActivityCreated");
    }

    /**
     * 开始执行与控件相关的逻辑代码，如按键点击
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, TAG+"--onStart");
    }

    /**
     * 这是Fragment从创建到显示的最后一个回调的方法
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, TAG+"--onResume");
    }

    /**
     * 当发生界面跳转时，临时暂停，暂停时间是500ms，0.5s 后直接进入下面的onStop方法
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, TAG+"--onPause");
    }

    /**
     * 当该方法返回时，Fragment将从屏幕上消失
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, TAG+"--onStop");
    }

    /**
     * 当 fragment 状态被保存，或者从回退栈弹出，该方法被调用
     */
    @Override public void onDestroyView() {
        Log.d(TAG, TAG + "--onDestroyView");
        super.onDestroyView();
    }

    /**
     *当该Fragment不再被使用时，如按返回键，就会调用此方法
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, TAG + "--onDestroy");
        super.onDestroy();
    }

    /**
     * 当该Fragment从它所属的Activity中被删除时调用该方法。
     * Fragment 生命周期的最后一个方法，执行完后将不再与 Activity 关联，将释放所有 fragment 对象和资源
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, TAG + "--onDetach");
    }
    class MyOnRefreshListener implements SwipyRefreshLayout.OnRefreshListener {
/*
        @Override
        public void onRefresh() {
            new RefreshDataTask().execute();
        }
*/
        @Override
        public void onRefresh(SwipyRefreshLayoutDirection direction) {
            if(direction == SwipyRefreshLayoutDirection.TOP) {
                new RefreshDataTask().execute();
            } else if(direction == SwipyRefreshLayoutDirection.BOTTOM) {
                new LoadMoreDataTask().execute();
            }
        }
    }
    class RefreshDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean blnResult = false;
            try {
                intSelectionOfLoadMoreData = 0;
                blnResult = true;
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return blnResult;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result) mHandler.sendEmptyMessage(1);
            else mHandler.sendEmptyMessage(2);
        }
    }
    class LoadMoreDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean blnResult = false;
            try {
                intSelectionOfLoadMoreData = dataList.size() > 0 ? dataList.size()-1 : 0;
                for(int i=0;i<20;i++) {
                    if(listUrls.isEmpty()) break;
                    dataList.add(listUrls.remove(0));
                    blnResult = true;
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return blnResult;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result) mHandler.sendEmptyMessage(1);
            else mHandler.sendEmptyMessage(2);
        }
    }

    static class MyHandler extends Handler {
        WeakReference<DrawerLayoutFrag> mActivity;
        MyHandler(DrawerLayoutFrag activity) {
            mActivity = new WeakReference<DrawerLayoutFrag>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            DrawerLayoutFrag theFragment = mActivity.get();
            if (theFragment == null) return;
            switch (msg.what) {
                case 1:
                    theFragment.mDrawerLayoutFragAdapter.notifyDataSetChanged();
                    theFragment.listView.setSelection(theFragment.intSelectionOfLoadMoreData);
                    theFragment.mSwipeLayout.setRefreshing(false);
                    break;
                case 2:
                    theFragment.mSwipeLayout.setRefreshing(false);
                    Toast.makeText(theFragment.mCallBack.getMyApp(),"没有更多数据",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
    private List<String> getData(){

        List<String> data = new ArrayList<String>();
        for(int i=0;i<100;i++) {
            data.add("测试数据 "+ (i+1));
        }
        return data;
    }
}