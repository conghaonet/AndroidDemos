package com.app2m.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyDaemonService extends Service {
	private static final String TAG = MyDaemonService.class.getName();
	public MyDaemonService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "MyDaemonService.onBind() is processing");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "MyDaemonService.onCreate() is processing");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "MyDaemonService.onStartCommand() is processing flags = " + flags);
		Toast.makeText(this, "MyDaemonService.onStartCommand()", Toast.LENGTH_SHORT).show();
		MyIntentService.startActionFoo(this, "param1", "param2");
		return super.onStartCommand(intent, START_STICKY, startId);
	}
	@Override
	public void onDestroy() {
		Log.d(TAG, "MyDaemonService.onDestroy() is processing");
		Toast.makeText(this, "MyDaemonService.onDestroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
}
