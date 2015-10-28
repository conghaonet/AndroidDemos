package com.app2m.demo.mymp4parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 图片加载类
 * 
 * @author 月月鸟
 */
public class ImageManager2 {
	private volatile static ImageManager2 mInstance;
	private Context mContext;
	private Bitmap emptyFrame;
	/** 图片加载队列，后进先出 */
	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();
	/** 图片请求队列，先进先出，用于存放已发送的请求。 */
	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();
	/** 图片加载线程消息处理器 */
	private Handler mImageLoaderHandler;
	/** 图片加载线程是否就绪 */
	private boolean mImageLoaderIdle = true;
	/** 请求图片 */
	private static final int MSG_REQUEST = 1;
	/** 图片加载完成 */
	private static final int MSG_REPLY = 2;
	/** 中止图片加载线程 */
	private static final int MSG_STOP = 3;
	/** 如果图片是从网络加载，则应用渐显动画，如果从缓存读出则不应用动画 */
	private boolean isFromNet = true;

	/**
	 * 获取单例，只能在UI线程中使用。
	 * 
	 * @param context
	 * @return
	 */
	public static ImageManager2 from(Context context, Bitmap emptyFrame) {
		// 如果不在ui线程中，则抛出异常
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}
		if (mInstance == null) {
			synchronized(MyMemoryCache.class) {
				if (mInstance == null) {
					mInstance = new ImageManager2(context, emptyFrame);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 私有构造函数，保证单例模式
	 * 
	 * @param context
	 */
	private ImageManager2(Context context, Bitmap emptyFrame) {
		if(this.emptyFrame != emptyFrame) {
			if(this.emptyFrame != null && !this.emptyFrame.isRecycled()) {
				this.emptyFrame.recycle();
			}
			this.emptyFrame = emptyFrame;
		}
		if(mContext == null) {
			WeakReference<Context> weakContext = new WeakReference<>(context);
			mContext = weakContext.get();
/*
			if(context instanceof MyApp) {
				mContext = (MyApp)context;
			} else {
				mContext = (MyApp)context.getApplicationContext();
			}
*/
		}

	}

	/**
	 * 存放图片信息
	 */
	class ImageRef {
		ImageView imageView;
		int width = 0;
		int height = 0;
		MediaEntity entity;
		String url;
		ImageRef(ImageView imageView, String url, int width, int height, MediaEntity entity) {
			this.imageView = imageView;
			this.entity = entity;
			this.width = width;
			this.height = height;
			this.url = url;
		}
	}

	/**
	 * 显示图片固定大小图片的缩略图，一般用于显示列表的图片，可以大大减小内存使用
	 * 
	 * @param imageView 加载图片的控件
	 * @param width 指定宽度
	 * @param height 指定高度
	 */
	public void displayImage(ImageView imageView, int width, int height, MediaEntity entity) {
		if(this.mContext == null) return;
		if (imageView == null) return;
		if (entity == null || entity.equals("")) return;
		if(width <= 0 || height <=0) return;
		// 添加url tag
		imageView.setTag(entity.getFilepath());
		ImageRef imageRef = new ImageRef(imageView, entity.getFilepath(), width, height, entity);
		Bitmap bitmap = MyMemoryCache.getInstance().get(getCacheKey(imageRef));
		if (bitmap != null && !bitmap.isRecycled()) {
			setImageBitmap(imageView, bitmap, false);
		} else {
			if(this.emptyFrame != null && !this.emptyFrame.isRecycled()) imageView.setImageBitmap(this.emptyFrame);
			queueImage(imageRef);
		}
	}
	private String getCacheKey(ImageRef imageRef) {
		return imageRef.entity.getFilepath()+"_"+imageRef.entity.getDuration()+"_"+imageRef.entity.getSize()+"_"+imageRef.entity.getMimeType();
	}
	/**
	 * 入队，后进先出
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {
		// 删除已有ImageView
		Iterator<ImageRef> iterator = mImageQueue.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().imageView == imageRef.imageView) {
				iterator.remove();
			}
		}
		// 添加请求
		mImageQueue.push(imageRef);
		sendRequest();
	}

	/**
	 * 发送请求
	 */
	private void sendRequest() {
		// 开启图片加载线程
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(
					imageLoader.getLooper());
		}
		// 发送请求
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST,
					imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}

	/**
	 * 图片加载线程
	 */
	class ImageLoaderHandler extends Handler {
		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
					case MSG_REQUEST: // 收到请求
						Bitmap bitmap = null;
						if (msg.obj != null && msg.obj instanceof ImageRef) {
							ImageRef imageRef = (ImageRef) msg.obj;
							bitmap = ThumbnailUtils.createVideoThumbnail(imageRef.url, MediaStore.Images.Thumbnails.MINI_KIND);
							if (bitmap != null && !bitmap.isRecycled()) {
								if (bitmap.getWidth() != imageRef.width || bitmap.getHeight() != imageRef.height) {
									bitmap = ThumbnailUtils.extractThumbnail(bitmap, imageRef.width, imageRef.height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
								}
								if(ImageManager2.this.mContext == null) {
									bitmap.recycle();
									break;
								} else {
									MyMemoryCache.getInstance().put(getCacheKey(imageRef), bitmap);
								}
							}
						}
						if (mImageManagerHandler != null) {
							Message message = mImageManagerHandler.obtainMessage(MSG_REPLY, bitmap);
							mImageManagerHandler.sendMessage(message);
						}
						break;
					case MSG_STOP: // 收到终止指令
						Looper.myLooper().quit();
						break;
					default:
						break;
				}
			}
		}
	}

	/** UI线程消息处理器 */
	private Handler mImageManagerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
				case MSG_REPLY: // 收到应答
					do {
						ImageRef imageRef = mRequestQueue.remove();
						if (imageRef == null) break;
						if (imageRef.imageView == null || imageRef.imageView.getTag() == null || imageRef.url == null) break;
						// 非同一ImageView
						if (!(imageRef.url).equals(imageRef.imageView.getTag())) break;
						if (!(msg.obj instanceof Bitmap) || msg.obj == null) break;
						Bitmap bitmap = (Bitmap) msg.obj;
						setImageBitmap(imageRef.imageView, bitmap, isFromNet);
						isFromNet = false;
					} while (false);
					break;
				}
			}
			// 设置闲置标志
			mImageLoaderIdle = true;
			// 若服务未关闭，则发送下一个请求。
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};

	/**
	 * 添加图片显示渐现动画
	 * 
	 */
	private void setImageBitmap(ImageView imageView, Bitmap bitmap, boolean isTran) {
		if (isTran) {
			if(this.mContext == null) return;
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(this.mContext.getResources(),bitmap) });
			td.setCrossFadeEnabled(true);
			imageView.setImageDrawable(td);
			td.startTransition(300);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * Activity#onStop后，ListView不会有残余请求。
	 */
	public void stop() {
		// 清空请求队列
		mImageQueue.clear();
	}
}
