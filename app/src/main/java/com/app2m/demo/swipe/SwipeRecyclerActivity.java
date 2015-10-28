package com.app2m.demo.swipe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.app2m.demo.MyApp;
import com.app2m.demo.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SwipeRecyclerActivity extends AppCompatActivity {
    private static final String TAG = SwipeRecyclerActivity.class.getName();
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    protected LayoutManagerType mCurrentLayoutManagerType;
    private static final int SPAN_COUNT = 2;
    protected RadioButton mLinearLayoutRadioButton;
    protected RadioButton mGridLayoutRadioButton;
    private List<String> listUrls;
    private List<String> dataList;
    private SwipeAdapter adapter;
    private SwipeRefreshLayout mSwipeLayout;
    private int intSelectionOfLoadMoreData;
    private MyHandler mHandler;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_recycler);
        this.mHandler = new MyHandler(this);
        this.myApp = (MyApp)this.getApplicationContext();
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);
//		mRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        Log.d(TAG, "mRecyclerView.isHardwareAccelerated = " + mRecyclerView.isHardwareAccelerated());
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType)savedInstanceState.getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mLinearLayoutRadioButton = (RadioButton)findViewById(R.id.list_layout_rb);
        mLinearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            }
        });

        mGridLayoutRadioButton = (RadioButton)findViewById(R.id.grid_layout_rb);
        mGridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }
        });
        listUrls = new ArrayList<String>();
        Collections.addAll(listUrls, MyApp.ARRAY_IMAGE_URL);
        dataList = new ArrayList<String>();
        adapter = new SwipeAdapter(this.myApp, this.listUrls, this.dataList);
        mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
		mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.recycler_view_swipe);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RefreshDataTask().execute();
            }
        });
        //设置刷新时动画的颜色，可以设置4个
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		new RefreshDataTask().execute();
    }
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	@Override
	public void onBackPressed() {
		this.finish();
	}
	@Override
	public void onStop() {
		super.onStop();
		adapter.stop();
	}
    class RefreshDataTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
			List<String> tempData = new ArrayList<String>();

                myApp.getMemCache().evictAll();
                if(dataList.isEmpty()) {
                    for(int i=0;i<20;i++) {
                        if(listUrls.isEmpty()) break;
						tempData.add(listUrls.remove(0));
                    }
                }
/*
			try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
            return tempData;
        }
        @Override
        protected void onPostExecute(List<String> tempData) {
			if(tempData != null) {
				dataList.addAll(tempData);
				adapter.notifyDataSetChanged();
			}
			mSwipeLayout.setRefreshing(false);
//			Toast.makeText(SwipeRecyclerActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
//				mRecyclerView.scrollToPosition(intSelectionOfLoadMoreData);
        }
    }
    static class MyHandler extends Handler {
        WeakReference<SwipeRecyclerActivity> mActivity;
        MyHandler(SwipeRecyclerActivity activity) {
            mActivity = new WeakReference<SwipeRecyclerActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            SwipeRecyclerActivity theActivity = mActivity.get();
            if(theActivity == null) return;
            switch (msg.what) {
                default:
                    break;
            }
        }
    }
}
