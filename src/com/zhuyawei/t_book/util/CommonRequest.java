package com.zhuyawei.t_book.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.zhuyawei.t_book.TBookApplication;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanamingming on 16/2/28.
 */
public class CommonRequest extends StringRequest{

	public static String JSESSIONID=null;

	public CommonRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	public CommonRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();

		if(headers!=null || headers.equals(Collections.emptyMap())){
			headers = new HashMap<String, String>();
		}
		//濡傛灉鏈塻ession鍒欎紶閫抯essionid
		if(JSESSIONID!=null) {
			headers.put("Cookie", JSESSIONID);
		}
		//鍒ゆ柇sharepreference涓槸鍚﹀瓨鏈塩art淇℃伅  鏈夌殑璇濆垯涓�捣鍙戦�鏁版嵁
		SharedPreferences pref = TBookApplication.getContext().getSharedPreferences("cart", Context.MODE_PRIVATE);
		String cart=pref.getString("cart", null);
		if(cart!=null){
			String cookie=headers.get("Cookie");
			headers.put("Cookie", cookie==null ? cart:cookie+", cart="+cart);
		}
		return headers;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		Map<String, String> headers = response.headers;
		String sessionid=headers.get("Set-Cookie");
		if(sessionid!=null){
			JSESSIONID=sessionid.split(";")[0];
		}
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}
}
