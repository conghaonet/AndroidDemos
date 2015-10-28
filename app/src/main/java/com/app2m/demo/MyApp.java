package com.app2m.demo;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;

import com.app2m.demo.service.TimeTickReceiver;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Cong Hao on 2015/5/20.
 * Email: hao.cong@qq.com
 */
public class MyApp extends Application {
	private  static final String TAG = MyApp.class.getName();

	@Override
	public void onCreate() {
		super.onCreate();
		//fro BaiduLBS
		SDKInitializer.initialize(getApplicationContext());
		getMemCache();
		registerReceivers();
	}
	private void registerReceivers() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
		TimeTickReceiver receiver = new TimeTickReceiver();
		registerReceiver(receiver, filter);
	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	private volatile LruCache<String, Bitmap> mLruCache;
	private final Object mLruCacheLock = new Object();
	private static final int SCREENS_OF_MEMORY_CACHE = 4;
	public LruCache<String, Bitmap> getMemCache() {
		if(this.mLruCache == null) {
			synchronized(this.mLruCacheLock) {
				if(this.mLruCache == null) {
					this.mLruCache = new LruCache<String, Bitmap>(getCacheSize(SCREENS_OF_MEMORY_CACHE)) {
						@Override
						protected int sizeOf(String key, Bitmap bitmap) {
							return bitmap.getByteCount();
						}
					};
				}
				this.mLruCacheLock.notifyAll();
			}
		}
		return this.mLruCache;
	}
	private int getCacheSize(int intScreens) {
		if(intScreens < 1 ) intScreens = 1;
		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		final int screenWidth = displayMetrics.widthPixels;
		final int screenHeight = displayMetrics.heightPixels;
		// 4 bytes per pixel
		final int screenBytes = screenWidth * screenHeight * 4;
		final int cacheSizeOfScreens = screenBytes * intScreens;

		//获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
//        final int memClass = ((ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
//        final int cacheSize = 1024 * 1024 * memClass / 8;
		final int cacheSizeOfMaxMemory = (int)(Runtime.getRuntime().maxMemory() / 8);
		return cacheSizeOfScreens < cacheSizeOfMaxMemory ? cacheSizeOfScreens : cacheSizeOfMaxMemory;
	}
	public int getVersionCode() {//获取版本号(内部识别号)
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pi.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public static final String[] ARRAY_IMAGE_URL = new String[] {
			"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_3127.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_9576.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_1721.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383219_5806.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383214_7794.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383213_4418.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383213_3557.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383210_8779.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383172_4577.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_3407.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_2224.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_7301.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383165_7197.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383150_8410.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383131_3736.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383130_5094.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383130_7393.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383129_8813.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383100_3554.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383093_7894.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383092_2432.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383092_3071.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383091_3119.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_6589.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_8814.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_2237.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383058_4330.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383038_3602.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_3079.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_8125.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_4881.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382941_4559.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382941_3845.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382924_8955.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382923_2141.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382923_8437.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382922_6166.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382922_4843.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382905_5804.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_3362.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_2312.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_4960.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382900_2418.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382881_4490.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382881_5935.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382880_3865.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382880_4662.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382879_2553.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382862_5375.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382862_1748.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_7618.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_8606.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_8949.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382841_9821.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_6603.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_2405.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_6354.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382839_5779.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382810_7578.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382810_2436.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382809_3883.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382809_6269.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382808_4179.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382790_8326.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_7174.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_5170.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_4118.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382788_9532.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382767_3184.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382767_4772.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382766_4924.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382766_5762.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382765_7341.jpg" };
}
