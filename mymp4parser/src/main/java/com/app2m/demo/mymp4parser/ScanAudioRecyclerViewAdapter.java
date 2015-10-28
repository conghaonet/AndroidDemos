package com.app2m.demo.mymp4parser;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Cong Hao on 2015/6/2.
 * Email: hao.cong@qq.com
 */
public class ScanAudioRecyclerViewAdapter extends RecyclerView.Adapter<ScanAudioRecyclerViewAdapter.ViewHolder> {
	private static final String TAG = ScanAudioRecyclerViewAdapter.class.getName();
	private Context context;
	private List<MediaEntity> dataList;
	private ContentResolver cr;
	private MyApp myApp;
	private Bitmap emptyFrameBitmap;
	private final int thumbnailWidth = 480;
	private final int thumbnailHeight= 270;
	private DecimalFormat decimalFormat;

	public ScanAudioRecyclerViewAdapter(Context context, List<MediaEntity> dataList) {
		this.decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		this.context = context;
		this.myApp = (MyApp)context.getApplicationContext();
		this.dataList = dataList;
		this.cr = this.context.getContentResolver();
		emptyFrameBitmap = myApp.getBitmapEmptyFrame(0, thumbnailWidth, thumbnailHeight);

	}
	@Override
	public ScanAudioRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		// Create a new view.
		View v = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recycler_view_audio_item, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		if(position >= dataList.size()) return;
		MediaEntity entity = dataList.get(position);
		viewHolder.getTxtName().setText("文件: "+entity.getFilepath().substring(entity.getFilepath().lastIndexOf(File.separator)+1, entity.getFilepath().lastIndexOf(".")));
		String fileSize = decimalFormat.format((float) entity.getSize() / 1024 / 1024);//format 返回的是字符串
		viewHolder.getTxtSize().setText("大小: " + fileSize +" MB");
		viewHolder.getTxtDuration().setText("时长: "+(entity.getDuration() / 1000) +" 秒");
		// 如果设置了回调，则设置点击事件
		if (mOnItemClickListener != null) {
			viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickListener.onItemClick(viewHolder.itemView, position);
				}
			});
		}

/*
		ImageManager2.from(myApp, emptyFrameBitmap).displayImage(viewHolder.getImgThumbnail(),
				filename, -1, thumbnailWidth, thumbnailHeight, null, false);
*/
/*
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE},
				MediaStore.Audio.Media.DATA + "='" + dataList.get(position) + "'", null, null);

		if(cursor.moveToFirst()) {
			int columnIndex = 0;
			columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
			viewHolder.getTxtName().setText("文件："+cursor.getString(columnIndex));
			columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			viewHolder.getTxtDuration().setText("时长："+cursor.getString(columnIndex));
			columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
			viewHolder.getTxtSize().setText("大小："+cursor.getString(columnIndex));
			// 如果设置了回调，则设置点击事件
			if (mOnItemClickListener != null) {
				viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mOnItemClickListener.onItemClick(viewHolder.itemView, position);
					}
				});
			}
		}
		if(cursor != null) cursor.close();
*/
	}

	@Override
	public int getItemCount() {
		return this.dataList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView txtName;
		private final TextView txtSize;
		private final TextView txtDuration;

		public ViewHolder(View itemView) {
			super(itemView);
/*
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "ViewHolder clicked at position = " + getAdapterPosition());
				}
			});
*/
			txtName = (TextView) itemView.findViewById(R.id.audio_item_filename_txt);
			txtSize = (TextView) itemView.findViewById(R.id.audio_item_size_txt);
			txtDuration = (TextView) itemView.findViewById(R.id.audio_item_duration_txt);
		}
		public TextView getTxtName() {
			return txtName;
		}
		public TextView getTxtSize() {
			return txtSize;
		}
		public TextView getTxtDuration() {
			return txtDuration;
		}
	}
/*
	private void openAudioPreview(int position) {
		String filename = dataList.get(position);
		Intent intent = new Intent();
		intent.setClass(context, VideoPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("audio_filename", filename);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
*/
	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}
	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
}
