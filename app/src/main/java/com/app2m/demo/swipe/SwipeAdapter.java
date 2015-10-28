package com.app2m.demo.swipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.app2m.demo.MyApp;
import com.app2m.demo.R;
import com.app2m.demo.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cong Hao on 2015/5/18.
 * Email: hao.cong@qq.com
 */
public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> {
    private static final String TAG = SwipeAdapter.class.getName();
    private List<String> dataList;
	private List<String> listUrls;
	private LoadingDataTask loadingDataTask;
    private MyApp myApp;

    public SwipeAdapter(Context context, List<String> listUrls, List<String> dataList) {
        myApp = (MyApp)context.getApplicationContext();
		this.listUrls = listUrls;
        this.dataList = dataList;
	}
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.swipe_item, viewGroup, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
		private final LinearLayout nomoreLayout;
		private final LinearLayout loadingLayout;
		private final LinearLayout contentLayout;
        private final TextView contentText;
        private final ImageView img;
        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition()+ " clicked.");
                }
            });
			nomoreLayout = (LinearLayout) v.findViewById(R.id.swipe_item_nomore_layout);
			loadingLayout = (LinearLayout) v.findViewById(R.id.swipe_item_loading_layout);
			contentLayout = (LinearLayout) v.findViewById(R.id.swipe_item_content_layout);
            contentText = (TextView) v.findViewById(R.id.swipe_item_text);
            img = (ImageView)v.findViewById(R.id.swipe_item_img);
		}
        public TextView getContentText() {
            return this.contentText;
        }
        public ImageView getImg() {
            return this.img;
        }
		public LinearLayout getContentLayout() {
			return this.contentLayout;
		}
		public LinearLayout getLoadingLayout() {
			return this.loadingLayout;
		}
		public LinearLayout getNomoreLayout() {
			return this.nomoreLayout;
		}
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");
		if(position == dataList.size()) {
			if(listUrls.isEmpty()) {
				viewHolder.getContentLayout().setVisibility(View.GONE);
				viewHolder.getLoadingLayout().setVisibility(View.GONE);
				viewHolder.getNomoreLayout().setVisibility(View.VISIBLE);
			} else {
				if(loadingDataTask==null || loadingDataTask.getStatus()== AsyncTask.Status.FINISHED) {
					viewHolder.getContentLayout().setVisibility(View.GONE);
					viewHolder.getLoadingLayout().setVisibility(View.VISIBLE);
					viewHolder.getNomoreLayout().setVisibility(View.GONE);
					loadingDataTask = new LoadingDataTask();
					loadingDataTask.execute();
				}
			}
		} else {
			viewHolder.getContentLayout().setVisibility(View.VISIBLE);
			viewHolder.getLoadingLayout().setVisibility(View.GONE);
			viewHolder.getNomoreLayout().setVisibility(View.GONE);
			final String imgUrl = dataList.get(position);
			if (!TextUtils.isEmpty(imgUrl)) {
				viewHolder.getContentText().setText("position = " + (position + 1));
				viewHolder.getImg().setTag(imgUrl);
				ImageRequest imageRequest = new ImageRequest(
						imgUrl,
						new Response.Listener<Bitmap>() {
							@Override
							public void onResponse(Bitmap response) {
								if(imgUrl.equals((String)viewHolder.getImg().getTag()))
									viewHolder.getImg().setImageBitmap(response);
							}
						}, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565,
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								viewHolder.getImg().setImageResource(R.mipmap.ic_launcher);
							}
						});
				imageRequest.setTag(TAG);
				VolleySingleton.getInstance(myApp).getRequestQueue().add(imageRequest);
			}
		}
    }
    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

	public void stop() {
		VolleySingleton.getInstance(myApp).getRequestQueue().cancelAll(TAG);
	}
	class LoadingDataTask extends AsyncTask<Void, Void, List<String>> {
		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> tempData = new ArrayList<String>();
			for(int i=0;i<20;i++) {
				if(listUrls.isEmpty()) break;
				tempData.add(listUrls.remove(0));
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return tempData;
		}
		@Override
		protected void onPostExecute(List<String> tempData) {
			if(tempData != null) {
				dataList.addAll(tempData);
				SwipeAdapter.this.notifyDataSetChanged();
			}
		}
	}
}
