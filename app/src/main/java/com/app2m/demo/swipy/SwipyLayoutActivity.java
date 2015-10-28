package com.app2m.demo.swipy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.app2m.demo.MyApp;
import com.app2m.demo.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SwipyLayoutActivity extends AppCompatActivity {
    private static final String TAG = SwipyLayoutActivity.class.getName();
//    private SwipeRefreshLayout mSwipeLayout;
    private SwipyRefreshLayout mSwipeLayout;
    private List<String> listUrls;
    private List<String> dataList;
    private SwipyAdapter mSwipyAdapter;
    private MyHandler mHandler;
    private int intSelectionOfLoadMoreData;
    private ListView listView;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipy);
        listUrls = new ArrayList<String>();
        Collections.addAll(listUrls, MyApp.ARRAY_IMAGE_URL);
		mHandler = new MyHandler(this);
//        mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.backlog_approval_swipe);
//        mSwipeLayout.setOnRefreshListener(new MyOnRefreshListener());
		mSwipeLayout = (SwipyRefreshLayout) findViewById(R.id.swipy_layout);
		mSwipeLayout.setOnRefreshListener(new MyOnRefreshListener());

		//设置刷新时动画的颜色，可以设置4个
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		listView = (ListView) findViewById(R.id.swipy_layout_listview);
			listView.setVisibility(View.VISIBLE);
			mSwipeLayout.setVisibility(View.VISIBLE);
			dataList = new ArrayList<String>();
			for(int i=0;i<20;i++) {
				if(listUrls.isEmpty()) break;
				dataList.add(listUrls.remove(0));
			}
			mSwipyAdapter = new SwipyAdapter(this, dataList);
			listView.setAdapter(mSwipyAdapter);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
	@Override
	public void onBackPressed() {
		this.finish();
	}
	@Override
	public void onStop() {
		super.onStop();
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
		WeakReference<SwipyLayoutActivity> mActivity;
		MyHandler(SwipyLayoutActivity activity) {
			mActivity = new WeakReference<SwipyLayoutActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			SwipyLayoutActivity theActivity = mActivity.get();
			if (theActivity == null) return;
			switch (msg.what) {
				case 1:
					theActivity.mSwipyAdapter.notifyDataSetChanged();
					theActivity.listView.setSelection(theActivity.intSelectionOfLoadMoreData);
					theActivity.mSwipeLayout.setRefreshing(false);
					break;
				case 2:
					theActivity.mSwipeLayout.setRefreshing(false);
					Toast.makeText(theActivity, "没有更多数据", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	}
}
