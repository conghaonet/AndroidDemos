package com.app2m.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonActivity extends AppCompatActivity {
    private AppCompatTextView textStr;
	private AppCompatTextView textCity;
	private AppCompatTextView textCityId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        textStr = (AppCompatTextView)findViewById(R.id.json_txt_str);
		textCity = (AppCompatTextView)findViewById(R.id.json_txt_city);
		textCityId = (AppCompatTextView)findViewById(R.id.json_txt_cityid);
//        RequestQueue mRequestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String strJson = "http://www.weather.com.cn/adat/sk/101010100.html";
//		String strJson = "http://192.168.1.180/mobileapproveserver/organizationjson";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(strJson, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            textStr.setText(response.toString());
                            JSONObject weatherObj = response.getJSONObject("weatherinfo");
                            textCity.setText(weatherObj.getString("city"));
							textCityId.setText(weatherObj.getString("cityid"));
/*
							textCity.setText(response.getString("code"));
							textCityId.setText(response.getString("name"));
*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textCity.setText(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textCity.setText(error.getMessage());
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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
        this.finish();
    }
}
