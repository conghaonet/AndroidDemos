package com.app2m.demo.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
	private static final String TAG = MyIntentService.class.getName();
	// TODO: Rename actions, choose action names that describe tasks that this
	// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
	private static final String ACTION_FOO = "com.app2m.demo.service.action.FOO";
	private static final String ACTION_BAZ = "com.app2m.demo.service.action.BAZ";

	// TODO: Rename parameters
	private static final String EXTRA_PARAM1 = "com.app2m.demo.service.extra.PARAM1";
	private static final String EXTRA_PARAM2 = "com.app2m.demo.service.extra.PARAM2";

	/**
	 * Starts this service to perform action Foo with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startActionFoo(Context context, String param1, String param2) {
		Intent intent = new Intent(context, MyIntentService.class);
		intent.setAction(ACTION_FOO);
		intent.putExtra(EXTRA_PARAM1, param1);
		intent.putExtra(EXTRA_PARAM2, param2);
		context.startService(intent);
	}

	/**
	 * Starts this service to perform action Baz with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startActionBaz(Context context, String param1, String param2) {
		Intent intent = new Intent(context, MyIntentService.class);
		intent.setAction(ACTION_BAZ);
		intent.putExtra(EXTRA_PARAM1, param1);
		intent.putExtra(EXTRA_PARAM2, param2);
		context.startService(intent);
	}

	public MyIntentService() {
		super("MyIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_FOO.equals(action)) {
				final String param1 = intent.getStringExtra(EXTRA_PARAM1);
				final String param2 = intent.getStringExtra(EXTRA_PARAM2);
				handleActionFoo(param1, param2);
			} else if (ACTION_BAZ.equals(action)) {
				final String param1 = intent.getStringExtra(EXTRA_PARAM1);
				final String param2 = intent.getStringExtra(EXTRA_PARAM2);
				handleActionBaz(param1, param2);
			}
		} else {
			throw new UnsupportedOperationException("intent is null");
		}
	}

	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionFoo(String param1, String param2) {
		int i=0;
		while (i<10) {
			++i;
			try {
				Log.d(TAG, "handleActionFoo.param1 = " + param1 + " loop times = " + i);
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle action Baz in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionBaz(String param1, String param2) {
		// TODO: Handle action Baz
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
