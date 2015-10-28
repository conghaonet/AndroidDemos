package com.app2m.demo.mymp4parser;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class ScanVideoActivity extends AppCompatActivity {
	private static final String TAG = ScanVideoActivity.class.getName();
	private RecyclerView mRecyclerView;
	private List<MediaEntity> dataList;
	private ScanVideoRecyclerViewAdapter adapter;
	private MyApp myApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_video);
		myApp = (MyApp)this.getApplicationContext();
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_video);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		dataList = new ArrayList<>();
		adapter = new ScanVideoRecyclerViewAdapter(this, dataList);
		adapter.setOnItemClickListener(new ScanVideoRecyclerViewAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				MediaEntity entity = dataList.get(position);
				Intent intent = new Intent();
				intent.setClass(ScanVideoActivity.this, VideoPreviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("video_filename", entity.getFilepath());
				bundle.putInt("video_duration", entity.getDuration());
				intent.putExtras(bundle);
				startActivity(intent);
				ScanVideoActivity.this.finish();
			}
		});
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(((MyApp)this.getApplicationContext()).getMyDrawable(R.drawable.my_list_divider)));
		new RefreshDataTask().execute();
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
			ContentResolver cr = ScanVideoActivity.this.getContentResolver();
			String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION,
					MediaStore.Video.Media.SIZE, MediaStore.Video.Media.MIME_TYPE};
			Cursor cur = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
			try {
				int colIndex;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					colIndex = cur.getColumnIndex(MediaStore.Video.Media.DATA);
					String strPath = cur.getString(colIndex);
					if(strPath!=null && (strPath.toLowerCase().endsWith(".mp4") || strPath.toLowerCase().endsWith(".mkv"))) {
						MediaEntity entity = new MediaEntity();
						entity.setFilepath(strPath);
						colIndex = cur.getColumnIndex(MediaStore.Video.Media.DURATION);
						entity.setDuration(cur.getInt(colIndex));
						colIndex = cur.getColumnIndex(MediaStore.Video.Media.SIZE);
						entity.setSize(cur.getInt(colIndex));
						colIndex = cur.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
						entity.setMimeType(cur.getString(colIndex));
						Log.d(TAG, entity.getFilepath()+" entity.setMimeType = "+entity.getMimeType());
						tempData.add(entity);
					}
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
		this.finish();
	}
}
