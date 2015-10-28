package com.app2m.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2015/5/12.
 * Email: hao.cong@qq.com
 */
public class ADemoFragment extends Fragment {
    private static final String TAG = ADemoFragment.class.getName();
    private ADemoActivityCallBack mCallBack;
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
        if (!(activity instanceof ADemoActivityCallBack)) {
            throw new IllegalStateException(TAG + " 所在的Activity必须实现接口：" + ADemoActivityCallBack.class.getName());
        }
        mCallBack = (ADemoActivityCallBack)activity;
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
        Log.d(TAG, TAG+"--onViewCreated");
    }

    /**
     * 在Activity.onCreate方法调用后会立刻调用此方法，表示窗口已经初始化完毕，此时可以调用控件了
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, TAG+"--onActivityCreated");
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
        Log.d(TAG, TAG+"--onDetach");
    }
}

