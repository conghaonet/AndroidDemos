package com.app2m.demo.mymp4parser;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
//import edu.mit.mobile.android.imagecache.ImageCache;
//import edu.mit.mobile.android.imagecache.ImageCache.OnImageLoadListener;

public class MainActivity extends Activity implements OnItemClickListener {

	private static final String TAG = MainActivity.class.getName();
	ListView mList;
	private Cursor mCursor;
	private final SparseArray<SoftReference<ImageView>> mImageViewsToLoad = new SparseArray<SoftReference<ImageView>>();
//	private ImageCache mCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		mCache = ImageCache.getInstance(this);
//		mCache.registerOnImageLoadListener(this);
		mList = (ListView) findViewById(R.id.list);
		mList.setOnItemClickListener(this);
		mCursor = getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Video.Media.DATE_MODIFIED + " desc");
		SimpleCursorAdapter adapter = new videoListAdapter(this,
				R.layout.video_listitem, mCursor,
				new String[] { MediaStore.Video.Media.TITLE },
				new int[] { R.id.video_title });
		mList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// 扫描新多媒体文件,添加到数据库中
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://"
						+ Environment.getExternalStorageDirectory()
						.getAbsolutePath())));
		return false;
	}
	public void openScanVideoActivity(View view) {
		startActivity(new Intent(this, ScanVideoActivity.class));
	}

	public void scanFiles(View view) {
		// 扫描新多媒体文件,添加到数据库中
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//				Uri.parse("file://"
//						+ Environment.getExternalStorageDirectory()
//						.getAbsolutePath())));
		MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/"}, null, null);
	}
	public void tryMediaMuxer(View view) {
		String strSdCardPath = Environment.getExternalStorageDirectory().getPath()+File.separator;
		String audioFile = strSdCardPath + "aa.mp4";
		String videoFile = strSdCardPath+"td.mp4";
		try {
			MediaExtractor extractorAudio = new MediaExtractor();
			extractorAudio.setDataSource(audioFile);
			MediaFormat mMediaFormatAudio = null;
			for(int i=0;i<extractorAudio.getTrackCount();i++) {
				mMediaFormatAudio = extractorAudio.getTrackFormat(i);
				if(mMediaFormatAudio.containsKey(MediaFormat.KEY_MIME)) {
					String mime = mMediaFormatAudio.getString(MediaFormat.KEY_MIME);
					if(mime.equals(MediaFormat.MIMETYPE_AUDIO_AAC)) {
						break;
					}
				}
			}
			MediaExtractor extractorVideo = new MediaExtractor();
			extractorVideo.setDataSource(videoFile);
			MediaFormat mMediaFormatVideo = null;
			for(int i=0;i<extractorVideo.getTrackCount();i++) {
				mMediaFormatVideo = extractorVideo.getTrackFormat(i);
				if(mMediaFormatVideo.containsKey(MediaFormat.KEY_MIME)) {
					String mime = mMediaFormatVideo.getString(MediaFormat.KEY_MIME);
					if(mime.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
						break;
					}
				}
			}

//			MediaCodec codec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
			MediaMuxer mMediaMuxer = new MediaMuxer(strSdCardPath+"MediaMuxer.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
			int audioTrackIndex = mMediaMuxer.addTrack(mMediaFormatAudio);
			int videoTrackIndex = mMediaMuxer.addTrack(mMediaFormatVideo);
			ByteBuffer inputBuffer = ByteBuffer.allocate(8192);
			inputBuffer = mMediaFormatAudio.getByteBuffer("csd-0");
			boolean finished = false;
			MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
			mMediaMuxer.start();
			mMediaMuxer.writeSampleData(audioTrackIndex, inputBuffer, bufferInfo);
			inputBuffer = mMediaFormatVideo.getByteBuffer("csd-0");
			mMediaMuxer.writeSampleData(videoTrackIndex, inputBuffer, bufferInfo);

/*
			while(!finished) {
				// getInputBuffer() will fill the inputBuffer with one frame of encoded
				// sample from either MediaCodec or MediaExtractor, set isAudioSample to
				// true when the sample is audio data, set up all the fields of bufferInfo,
				// and return true if there are no more samples.
				finished = getInputBuffer(inputBuffer, isAudioSample, bufferInfo);
				if (!finished) {
					int currentTrackIndex = isAudioSample ? audioTrackIndex : videoTrackIndex;
					mMediaMuxer.writeSampleData(currentTrackIndex, inputBuffer, bufferInfo);
				}
			};
*/
			mMediaMuxer.stop();
			mMediaMuxer.release();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void plusAudio(View view) {
		double startPosition = 10;
		double endPosition = 50;
		System.out.println("isoparser mux3");
		String strSdCardPath = Environment.getExternalStorageDirectory().getPath()+File.separator;
		String audioEnglish = strSdCardPath + "aa.mp4";
		String video = strSdCardPath+"td.mp4";
		Log.d(TAG, Environment.getExternalStorageDirectory().getPath());
		try {
			String outputFile = video.substring(0, video.lastIndexOf("."));
			outputFile = outputFile + "_output_" + startPosition + "-" + endPosition + ".mp4";
			ClipUtil.clipVideoAudio(video, audioEnglish, outputFile, startPosition, endPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}

/*
		Movie countVideo = null;
		Movie countAudioEnglish = null;
		try {
			countVideo = MovieCreator.build(video);
			countAudioEnglish = MovieCreator.build(audioEnglish);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Track audioTrackEnglish = countAudioEnglish.getTracks().get(0);
		countVideo.addTrack(audioTrackEnglish);
		try {
			Container out = new DefaultMp4Builder().build(countVideo);
			FileOutputStream fos = new FileOutputStream(new File(strSdCardPath+"hope.mp4"));
			out.writeContainer(fos.getChannel());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		if (mCursor.moveToPosition(position)) {
			int index = -1;
			index = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
			String path = null;
			if (index >= 0) {
				path = mCursor.getString(index);
				try {
					ClipUtil.clipVideo(path, 0, 50);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static final class ViewHolder {
		/** 视频名称 */
		TextView titleView;
		/** 视频时长 */
		TextView durationView;
		/** 文件大小 */
		TextView sizeView;
	}

	private class videoListAdapter extends SimpleCursorAdapter {

		/*
		 * constructor.
		 */
		public videoListAdapter(Context context, int layout, Cursor c,
								String[] from, int[] to) {
			super(context, layout, c, from, to);
		}

		@Override
		public int getCount() {
			return super.getCount();
		}

		@Override
		public Object getItem(int position) {
			return super.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return super.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			Cursor cursor = getCursor();
			cursor.moveToPosition(position);
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				holder.titleView = (TextView) view
						.findViewById(R.id.video_title);
				holder.durationView = (TextView) view
						.findViewById(R.id.video_duration);
				holder.sizeView = (TextView) view.findViewById(R.id.video_size);
			}
			view.setTag(holder);
			final ImageView iv = (ImageView) view.findViewById(R.id.thumbnail);
			int index = -1;
			index = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
			String path = null;
			if (index >= 0) {
				path = mCursor.getString(index);
				try {
//					Drawable draw = mCache.loadImage(position, Uri.parse(path),	120, 120);
					Drawable draw = null;
					if (draw != null) {
						iv.setBackground(draw);
					} else {
						mImageViewsToLoad.put(position,
								new SoftReference<ImageView>(iv));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			index = -1;
			index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
			String title = null;
			if (index >= 0) {
				title = cursor.getString(index);
				holder.titleView.setText(title);
			}
			index = -1;
			index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
			int duration;
			if (index >= 0) {
				duration = cursor.getInt(index);
				holder.durationView.setText(Util.durationFormat(duration));
			}
			index = -1;
			index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
			long size;
			if (index >= 0) {
				size = cursor.getLong(index);
				holder.sizeView.setText(Util.sizeFormat(size));
			}
			return view;

		}

	}
	@Override
	public void onBackPressed() {
		this.finish();
	}

}