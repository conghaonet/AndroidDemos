package com.app2m.demo.mymp4parser;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2015/5/30.
 */
public class MyApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

	}
	public Bitmap getBitmapEmptyFrame(float radius, int width, int height) {
		radius = radius * getDisplay().density;
		if(this.isTablet()) radius = radius * 2;
		Bitmap bitmapEmptyFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmapEmptyFrame);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		float strokeWidth = 3 * getDisplay().density;
		if(this.isTablet()) strokeWidth = strokeWidth * 2;
		paint.setStrokeWidth(strokeWidth);              //线宽
		paint.setStyle(Paint.Style.STROKE);
		float dash = 5 * getDisplay().density;
		if(this.isTablet()) dash = dash * 2;
		PathEffect effects = new DashPathEffect(new float[] { dash, dash}, 1);
		paint.setPathEffect(effects);
		RectF rectf = new RectF(0F, 0F, width, height);
		canvas.drawRoundRect(rectf, radius, radius, paint);
		return bitmapEmptyFrame;
	}
	public DisplayMetrics getDisplay() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = windowMgr.getDefaultDisplay();
		display.getMetrics(dm);
		return dm;
	}
	public boolean isTablet() {
		return (getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	public Drawable getMyDrawable(int resId) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return getResources().getDrawable(resId, null);
		else return getResources().getDrawable(resId);
	}
}
