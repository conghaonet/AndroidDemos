package com.app2m.demo.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app2m.demo.MainActivity;

public class AutoStartReceiver extends BroadcastReceiver {
	private static final String TAG = AutoStartReceiver.class.getName();
	public AutoStartReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			ComponentName comp = new ComponentName(context.getPackageName(), MainActivity.class.getName());
			context.startActivity(new Intent().setComponent(comp).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			Log.d(TAG, "MainActivity is start.");
		} else {
			Log.e(TAG, "Received unexpected intent " + intent.toString());
		}
	}
}
