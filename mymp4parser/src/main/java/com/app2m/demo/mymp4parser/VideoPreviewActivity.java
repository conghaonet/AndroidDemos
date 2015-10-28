package com.app2m.demo.mymp4parser;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;


public class VideoPreviewActivity extends AppCompatActivity {
	private VideoView videoView;
	private String videoFilename;
	private int videoDuration;
	private TextView filenameTextView;
	private MediaController mediaController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_preview);
		videoView=(VideoView)findViewById(R.id.videoView);
		this.videoFilename = this.getIntent().getExtras().getString("video_filename");
		this.videoDuration = this.getIntent().getExtras().getInt("video_duration");
		filenameTextView = (TextView) findViewById(R.id.preview_video_filename);
		filenameTextView.setText(this.videoFilename);
		File file=new File(this.videoFilename);
		if(!file.exists()){
			this.finish();
			return;
		}
		mediaController =new MediaController(this);
		//VideoView与MediaController进行关联
		videoView.setMediaController(mediaController);
		videoView.setVideoPath(file.getAbsolutePath());
//		mediaController.setMediaPlayer(videoView);
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
			}
		});
		//让VideiView获取焦点
		videoView.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_video_preview, menu);
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
	public void onBackPressed() {
		startActivity(new Intent(this, ScanVideoActivity.class));
		this.finish();
	}
	@Override
	protected void onPause() {
		videoView.stopPlayback();
		super.onPause();
	}
	public void openScanAudio(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ScanAudioActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("video_filename", this.videoFilename);
		bundle.putInt("video_duration", this.videoDuration);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
	}
}
