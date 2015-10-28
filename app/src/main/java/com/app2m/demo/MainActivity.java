package com.app2m.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.app2m.demo.drawer.DrawerLayoutActivity;
import com.app2m.demo.swipe.SwipeRecyclerActivity;
import com.app2m.demo.swipy.SwipyLayoutActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
	private Toolbar mToolbar;
	private ActionBar mActionBar;
	private boolean hasSetIconOfOverlow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		mActionBar = getSupportActionBar();
//		mActionBar.setDisplayShowTitleEnabled(false);
		forceDisplay3PointsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		displayIconInOverflow(menu);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public void onBackPressed() {
		this.finish();
	}
	/**
	 * 利用反射让隐藏在Overflow中的MenuItem显示Icon图标
	 * @param featureId
	 * @param menu
	 * onMenuOpened方法中调用
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			displayIconInOverflow(menu);
		}
		return super.onMenuOpened(featureId, menu);
	}
	/**
	 * 利用反射让隐藏在Overflow中的MenuItem显示Icon图标
	 * @param menu
	 */
	private void displayIconInOverflow(Menu menu) {
		if(hasSetIconOfOverlow) return;
		if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
			try {
				Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
				m.setAccessible(true);
				m.invoke(menu, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		hasSetIconOfOverlow = true;
	}
	/**
	 * 在有menu按键的手机上，强制显示toolbar(actionbar)上最右侧的三个点的溢出图标(Overflow)。
	 */
	private void forceDisplay3PointsMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void clearCache(View view) {
		VolleySingleton.getInstance(this).getRequestQueue().getCache().clear();
	}
	public void openTryColor(View view) {
		Intent intent = new Intent();
		intent.setClass(this, TryColorActivity.class);
		startActivity(intent);
	}
	public void openBaiduPoiSearch(View view) {
		Intent intent = new Intent();
		intent.setClass(this, BaiduPoiSearchActivity.class);
		startActivity(intent);
	}
	public void openTryJson(View view) {
		Intent intent = new Intent();
		intent.setClass(this, JsonActivity.class);
		startActivity(intent);
	}
	public void openSwipeRecycler(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SwipeRecyclerActivity.class);
		startActivity(intent);
	}
	public void openSwipy(View view) {
		Intent intent = new Intent();
		intent.setClass(this, SwipyLayoutActivity.class);
		startActivity(intent);
	}
	public void openDrawerLayout(View view) {
		Intent intent = new Intent();
		intent.setClass(this, DrawerLayoutActivity.class);
		startActivity(intent);
	}
	public void openDaemonService(View view) {
		Intent intent = new Intent();
		intent.setClass(this, DaemonServiceActivity.class);
		startActivity(intent);
	}
}
