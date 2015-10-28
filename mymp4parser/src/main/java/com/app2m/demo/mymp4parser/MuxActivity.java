package com.app2m.demo.mymp4parser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.io.File;


public class MuxActivity extends AppCompatActivity {
	private static final String TAG = MuxActivity.class.getName();
	private String videoFilename;
	private int videoDuration;
	private String audioFilename;
	private MediaPlayer mp;
	private int currentMusicPosition;
	private VideoView videoView;
	private MediaController mediaController;
	private RangeSeekBar<Integer> rangeSeekBar;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mux);
		this.videoFilename = this.getIntent().getExtras().getString("video_filename");
		this.videoDuration = this.getIntent().getExtras().getInt("video_duration");
		this.audioFilename = this.getIntent().getExtras().getString("audio_filename");
		this.videoView = (VideoView) findViewById(R.id.mux_video_view);
		TextView videoTextView = (TextView) findViewById(R.id.mux_video_filename);
		TextView audioTextView = (TextView) findViewById(R.id.mux_audio_filename);
		videoTextView.setText(videoFilename);
		audioTextView.setText(audioFilename.substring(audioFilename.lastIndexOf(File.separator)+1, audioFilename.lastIndexOf(".")));
		rangeSeekBar = (RangeSeekBar) findViewById(R.id.mux_range_seek_bar);
		int videoSecond = videoDuration / 1000;
		rangeSeekBar.setRangeValues(0, videoSecond);
		rangeSeekBar.setSelectedMinValue(videoSecond / 5);
		rangeSeekBar.setSelectedMaxValue(videoSecond * 4 / 5);
		File fileVideo = new File(this.videoFilename);
		mediaController =new MediaController(this);
		//VideoView与MediaController进行关联
		videoView.setMediaController(mediaController);
		videoView.setVideoPath(fileVideo.getAbsolutePath());


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_mux, menu);
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
	public void muxPlayAudio(View view) {
		if(this.audioFilename == null) return;
		try {
			if(this.mp == null) {
				File file = new File(this.audioFilename);
				this.mp = MediaPlayer.create(this, Uri.fromFile(file));
				this.mp.setLooping(true);
			}
			if(this.mp.isPlaying()) return;
			if(this.currentMusicPosition > 0 ) mp.seekTo(this.currentMusicPosition);
			mp.start();
		} catch(Exception e) {
			this.mp = null;
			e.printStackTrace();
		}
	}
	public void muxStopAudio(View view) {
		if(this.mp == null) return;
		try{
			if(this.mp.isPlaying()) {
				this.currentMusicPosition = this.mp.getCurrentPosition();
			}
			this.mp.stop();
			this.mp.release();
			this.mp = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void doMux(View view) {
		if(rangeSeekBar.getSelectedMaxValue() == rangeSeekBar.getSelectedMinValue()) {
			Toast.makeText(this, "亲，最大值和最小值一样的话就啥都没有啦！", Toast.LENGTH_SHORT).show();
		} else {
			progressDialog = ProgressDialog.show(this, "制作MV", "多媒体文件处理中...", true, false);
			muxStopAudio(null);
			videoView.stopPlayback();
			new ExecuteMuxATask().execute();
		}
	}
	@Override
	protected void onPause() {
		muxStopAudio(null);
		videoView.stopPlayback();
		super.onPause();
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(this, ScanAudioActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("video_filename", videoFilename);
		bundle.putInt("video_duration", this.videoDuration);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
	}
	class ExecuteMuxATask extends AsyncTask<Void, Void, Long> {
		private String outputFile;
		@Override
		protected Long doInBackground(Void... params) {
			outputFile = Environment.getExternalStorageDirectory().getPath()+File.separator;
			outputFile = outputFile + videoFilename.substring(videoFilename.lastIndexOf(File.separator) + 1, videoFilename.lastIndexOf("."));
			outputFile = outputFile + "_output_" + rangeSeekBar.getSelectedMinValue()+"-" + rangeSeekBar.getSelectedMaxValue()+".mp4";
			File file = new File(outputFile);
			if(file.exists()) file.delete();
			long elapsedTime = 0;
			try {
				elapsedTime = ClipUtil.clipVideoAudio(videoFilename, audioFilename, outputFile, rangeSeekBar.getSelectedMinValue(), rangeSeekBar.getSelectedMaxValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return elapsedTime;
		}
		@Override
		protected void onPostExecute(Long  elapsedTime) {
			progressDialog.dismiss();
			if(elapsedTime <= 0) {
				Toast.makeText(MuxActivity.this, "抱歉，MV制作不成功，请尝试其它媒体文件。", Toast.LENGTH_SHORT).show();
			} else {
				long elapsedTimeSec = elapsedTime / 1000 ;
				if(elapsedTimeSec <= 0) elapsedTimeSec = 1;
				String finishedMsg = "MV制作完成啦！ 耗时 "+elapsedTimeSec+" 秒";
//				Toast.makeText(MuxActivity.this, "MV制作完成啦！ 耗时 "+elapsedTimeSec+" 秒", Toast.LENGTH_SHORT).show();
				final AlertDialog.Builder builder = new AlertDialog.Builder(MuxActivity.this);
				builder.setMessage(finishedMsg);
				builder.setTitle("完成");
				builder.setPositiveButton("播放MV", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(MuxActivity.this, CompleteActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("complete_filename",outputFile);
						intent.putExtras(bundle);
						startActivity(intent);
						MuxActivity.this.finish();
					}
				});
/*
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
*/
				builder.create().show();
			}
		}
	}
}
