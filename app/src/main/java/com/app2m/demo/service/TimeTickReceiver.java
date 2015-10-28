package com.app2m.demo.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Cong Hao on 2015/5/26.
 * Email: hao.cong@qq.com
 */
public class TimeTickReceiver extends BroadcastReceiver {
	private static final String TAG = TimeTickReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isServiceRunning = false;
		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			//检查Service状态
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (MyDaemonService.class.getName().equals(service.service.getClassName())) {
					Log.d(TAG, service.service.getClassName()+" is running");
					isServiceRunning = true;
					break;
				}
			}
			if (!isServiceRunning) {
				Log.d(TAG, MyDaemonService.class.getName() + " not running, will be restart.");
				Intent serviceIntent = new Intent(context, MyDaemonService.class);
				serviceIntent.setAction("Start from receiver!");
				context.startService(serviceIntent);
//				MyIntentService.startActionFoo(context, "pppp1", "pppp2");
//				Intent i = new Intent(context, xxxService.class);
//				context.startService(i);
			}
		}
	}
}
