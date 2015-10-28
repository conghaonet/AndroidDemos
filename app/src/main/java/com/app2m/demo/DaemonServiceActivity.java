package com.app2m.demo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app2m.demo.service.MyDaemonService;


public class DaemonServiceActivity extends AppCompatActivity {
	private static final String TAG = DaemonServiceActivity.class.getName();
	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daemon_service);
		textView = (TextView) findViewById(R.id.daemon_service_activity_text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main2, menu);
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
	public void onDestroy() {
		super.onDestroy();
	}
	public void startService(View view) {
		this.startService(new Intent(this, MyDaemonService.class));
		textView.setText(MyDaemonService.class.getName() + " is start.");

	}
	public void stopService(View view) {
		this.startService(new Intent(this, MyDaemonService.class));
		textView.setText(MyDaemonService.class.getName() + " is stopp.");

	}
}
