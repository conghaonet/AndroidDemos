package com.app2m.demo.mymp4parser;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ScanAudioActivity extends AppCompatActivity {
	private static final String TAG = ScanAudioActivity.class.getName();
	private RecyclerView mRecyclerView;
	private List<MediaEntity> dataList;
	private ScanAudioRecyclerViewAdapter adapter;
	private MyApp myApp;
	private String videoFilename;
	private int videoDuration;
	private TextView textViewAudioFilename;
	private Button btnAudioPlay;
	private Button btnAudioStop;
	private MediaPlayer mp;
	private String strSelectedAudioName;
	private int currentMusicPosition;
	private Button btnOpenMuxActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_audio);
		myApp = (MyApp)this.getApplicationContext();
		this.videoFilename = this.getIntent().getExtras().getString("video_filename");
		this.videoDuration = this.getIntent().getExtras().getInt("video_duration");
		TextView textViewVideoFilename = (TextView) findViewById(R.id.video_filename);
		textViewVideoFilename.setText(videoFilename);
		textViewAudioFilename = (TextView) findViewById(R.id.audio_filename);
		btnOpenMuxActivity = (Button) findViewById(R.id.btn_open_mux_activity);
//		btnOpenMuxActivity.setClickable(false);
		btnOpenMuxActivity.setVisibility(View.INVISIBLE);
		btnAudioPlay = (Button) findViewById(R.id.audio_btn_play);
		btnAudioPlay.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				playAudio();
			}
		});
		btnAudioStop = (Button) findViewById(R.id.audio_btn_stop);
		btnAudioStop.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAudio();
			}
		});

		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_video);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		dataList = new ArrayList<>();
		adapter = new ScanAudioRecyclerViewAdapter(this, dataList);
		adapter.setOnItemClickListener(new ScanAudioRecyclerViewAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				MediaEntity entity = dataList.get(position);
				if (entity.getFilepath() != strSelectedAudioName) {
					stopAudio();
					currentMusicPosition = 0;
					strSelectedAudioName = entity.getFilepath();
					playAudio();
					String tempAudioFileName = entity.getFilepath().substring(entity.getFilepath().lastIndexOf(File.separator) + 1, entity.getFilepath().lastIndexOf("."));
					textViewAudioFilename.setText(tempAudioFileName);
				}
				btnOpenMuxActivity.setVisibility(View.VISIBLE);
			}
		});
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(((MyApp)this.getApplicationContext()).getMyDrawable(R.drawable.my_list_divider)));
		new RefreshDataTask().execute();
	}
	private void playAudio() {
		if(strSelectedAudioName == null) return;
		try {
			if(ScanAudioActivity.this.mp == null) {
				File file = new File(strSelectedAudioName);
				ScanAudioActivity.this.mp = MediaPlayer.create(ScanAudioActivity.this, Uri.fromFile(file));
				ScanAudioActivity.this.mp.setLooping(true);
			}
			if(ScanAudioActivity.this.mp.isPlaying()) return;
			if(ScanAudioActivity.this.currentMusicPosition > 0 ) mp.seekTo(ScanAudioActivity.this.currentMusicPosition);
			mp.start();
		} catch(Exception e) {
			ScanAudioActivity.this.mp = null;
			e.printStackTrace();
		}
	}
	private void stopAudio() {
		if(ScanAudioActivity.this.mp == null) return;
		try{
			if(ScanAudioActivity.this.mp.isPlaying()) {
				ScanAudioActivity.this.currentMusicPosition = ScanAudioActivity.this.mp.getCurrentPosition();
			}
			ScanAudioActivity.this.mp.stop();
			ScanAudioActivity.this.mp.release();
			ScanAudioActivity.this.mp = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void openMuxActivity(View view) {
		if(strSelectedAudioName == null) return;
//		stopAudio();
		Intent intent = new Intent();
		intent.setClass(this, MuxActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("video_filename", videoFilename);
		bundle.putInt("video_duration", this.videoDuration);
		bundle.putString("audio_filename", strSelectedAudioName);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_scan_video, menu);
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
	class RefreshDataTask extends AsyncTask<Void, Void, List<MediaEntity>> {
		@Override
		protected List<MediaEntity> doInBackground(Void... params) {
			List<MediaEntity> tempData = new ArrayList<>();
			ContentResolver cr = ScanAudioActivity.this.getContentResolver();
			String[] proj = {MediaStore.Audio.Media.DATA};
			//MediaStore.Audio.Media.MIME_TYPE+"='audio/mp4'"
			Cursor cur = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.MIME_TYPE+"='audio/mp4'", null, null);
			try {
				int colIndex;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					MediaEntity entity = new MediaEntity();
					colIndex = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
					entity.setFilepath(cur.getString(colIndex));
					colIndex = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
					entity.setDuration(cur.getInt(colIndex));
					colIndex = cur.getColumnIndex(MediaStore.Audio.Media.SIZE);
					entity.setSize(cur.getInt(colIndex));
					colIndex = cur.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
					entity.setMimeType(cur.getString(colIndex));
					Log.d(TAG, entity.getFilepath()+" getMimeType=" + entity.getMimeType());
					tempData.add(entity);
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(cur != null) cur.close();
			}
			return tempData;
		}
		@Override
		protected void onPostExecute(List<MediaEntity> tempData) {
			if(tempData != null) {
				dataList.addAll(tempData);
				adapter.notifyDataSetChanged();
			}
		}
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(this, VideoPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("video_filename", this.videoFilename);
		bundle.putInt("video_duration", this.videoDuration);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
	}
	@Override
	protected void onPause() {
		stopAudio();
		super.onPause();
	}
}
