package com.app2m.demo.drawer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app2m.demo.MyApp;
import com.app2m.demo.R;

/**
 * Created by Cong Hao on 2015/5/12.
 * Email: hao.cong@qq.com
 */
public class DrawerLayoutViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = DrawerLayoutViewPagerAdapter.class.getName();
    private MyApp myApp;
    private String[] arrTitles;

    public DrawerLayoutViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        if(context instanceof MyApp) {
            myApp = (MyApp)context;
        } else {
            myApp = (MyApp)context.getApplicationContext();
        }
        this.arrTitles = myApp.getResources().getStringArray(R.array.category_backlog_title);
    }
    @Override
    public Fragment getItem(int arg0) {
        DrawerLayoutFrag mBacklogApprovalFrag = new DrawerLayoutFrag();
        mBacklogApprovalFrag.setPosition(arg0);
        return mBacklogApprovalFrag;
    }
    @Override
    public int getCount() {
        return arrTitles.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return arrTitles[position];
    }
/*
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = listFragments.get(position);
        if(!fragment.isAdded()){
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commit();
            mFragmentManager.executePendingTransactions();
        }
        if(fragment.getView().getParent() == null){
            container.addView(fragment.getView()); // 为viewpager增加布局
        }
        Log.i(TAG, "instantiateItem() [position: " + position + "]");
        return fragment.getView();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(listFragments.get(position).getView());
        Log.i(TAG, "destroyItem() [position: " + position + "]");
    }
*/
}
