package com.app2m.demo.mymp4parser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;


public class CompleteActivity extends AppCompatActivity {
	private String completeFilename;
	private TextView filenameTextView;
	private VideoView videoView;
	private MediaController mediaController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete);
		completeFilename = getIntent().getExtras().getString("complete_filename");
		filenameTextView = (TextView) findViewById(R.id.complete_video_filename);
		filenameTextView.setText(completeFilename);
		videoView=(VideoView)findViewById(R.id.complete_videoview);
		File file=new File(this.completeFilename);
		if(!file.exists()){
			this.finish();
			return;
		}
		mediaController =new MediaController(this);
		//VideoView与MediaController进行关联
		videoView.setMediaController(mediaController);
		videoView.setVideoPath(file.getAbsolutePath());
//		mediaController.setMediaPlayer(videoView);
		videoView.start();
		//让VideiView获取焦点
		videoView.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_complete, menu);
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
	public void reopenScanVideoActivity(View view) {
		startActivity(new Intent(this, ScanVideoActivity.class));
		finish();
	}
	@Override
	public void onBackPressed() {
		this.finish();
	}
}
