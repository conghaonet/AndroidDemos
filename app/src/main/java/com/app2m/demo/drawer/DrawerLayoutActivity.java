
package com.app2m.demo.drawer;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.app2m.demo.MyApp;
import com.app2m.demo.R;
import com.example.android.common.view.SlidingTabLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cong Hao on 2015/5/11.
 * Email: hao.cong@qq.com
 */
public class DrawerLayoutActivity extends AppCompatActivity implements DrawerLayoutActivityCallBack {
    private static final String TAG = DrawerLayoutActivity.class.getName();
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private MyApp myApp;
    private FragmentManager mFragmentManager;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        this.myApp = (MyApp)getApplicationContext();
        this.mHandler = new MyHandler(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mFragmentManager = getSupportFragmentManager();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new MyActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer_content_desc,R.string.close_drawer_content_desc);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        ListView leftListView=(ListView) findViewById(R.id.drawer_left_listview);
        DrawerLayoutLeftMenuAdapter leftAdapter = new DrawerLayoutLeftMenuAdapter(this.getApplicationContext());
        leftListView.setAdapter(leftAdapter);
        ListView rightListView=(ListView) findViewById(R.id.drawer_right_listview);
        rightListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData()));

        mViewPager = (ViewPager) findViewById(R.id.drawer_sliding_tab_viewpager);
        mViewPager.setAdapter(new DrawerLayoutViewPagerAdapter(mFragmentManager,this.getMyApp()));
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.drawer_sliding_tab);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_sliding_tab, R.id.custom_sliding_tab_title);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(DrawerLayoutActivity.this, "home_button", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(DrawerLayoutActivity.this, "other", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean hasClosedRight;
    private class  MyActionBarDrawerToggle extends ActionBarDrawerToggle {
        public MyActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar,
                                       @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
        }
        @Override
        public void onDrawerOpened(View view) {
            super.onDrawerOpened(view);
        }
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
        }

        /**
         * DrawerLayout.STATE_IDLE:0, DrawerLayout.STATE_DRAGGING:1, DrawerLayout.STATE_SETTLING:2
         * @param newState
         */
        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if(newState == DrawerLayout.STATE_IDLE) hasClosedRight = false;
            if(newState == DrawerLayout.STATE_SETTLING && mDrawerLayout.isDrawerOpen(Gravity.RIGHT) && !hasClosedRight) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                hasClosedRight = true;
            }
        }
    }
    static class MyHandler extends Handler {
        WeakReference<DrawerLayoutActivity> mActivity;
        MyHandler(DrawerLayoutActivity activity) {
            mActivity = new WeakReference<DrawerLayoutActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            DrawerLayoutActivity theActivity = mActivity.get();
            if(theActivity == null) return;
            switch (msg.what) {
                default:
                    break;
            }
        }
    }
    @Override
    public MyApp getMyApp() {
        return this.myApp;
    }
    private List<String> getData(){

        List<String> data = new ArrayList<String>();
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");
        return data;
    }
}
