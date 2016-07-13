package com.zhuyawei.t_book.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.entity.User;
import com.zhuyawei.t_book.model.IUserModel;
import com.zhuyawei.t_book.util.CommonRequest;
import com.zhuyawei.t_book.util.GlobalConsts;
import com.zhuyawei.t_book.util.JSONParser;

public class UserModel implements IUserModel {

	private RequestQueue queue;

	public UserModel() {
		queue = Volley.newRequestQueue(TBookApplication.getContext());
	}

	@Override
	public void login(final String loginname, final String password, final AsyncCallback callback) {
		int mothod = Request.Method.POST;
		String url = GlobalConsts.URL_USER_LOGIN;
		
		Response.Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject object = new JSONObject(response);
					if(object.getInt("code") == GlobalConsts.RESPONSE_CODE_SUCCESS) {
						JSONObject userObject = object.getJSONObject("user");
						TBookApplication app = TBookApplication.getContext();
						app.setCurrentUser(JSONParser.parseUser(userObject));
						app.setToken(object.getString("token"));
						callback.onSuccess(null);
					}
					else {
						callback.onError(object.getString("error_msg"));
					}
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		
		Response.ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError("µÇÂ½Ê§°Ü");
			}
		};
		
		CommonRequest request = new CommonRequest(mothod, url, listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", loginname);
				params.put("password", password);
				return params;
			}
		};
		
		queue.add(request);
	}

	@Override
	public void regist(final User user, final String code, final AsyncCallback callback) {
		int method = Request.Method.POST;
		String url = GlobalConsts.URL_USER_REGIST;
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				callback.onSuccess(response);
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};
		CommonRequest request = new CommonRequest(method, url, listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user.email", user.getEmail());
				map.put("user.nickname", user.getNickname());
				map.put("user.password", user.getPassword());
				map.put("number", code);
				return map;
			}
		};
		
		queue.add(request);
 	}

	@Override
	public void getImageCode(final AsyncCallback callback) {
		String url = GlobalConsts.URL_GET_IMAGE_CODE;
		Listener<Bitmap> listener = new Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				callback.onSuccess(response);
			}
		};
		int maxWidth = 130;
		int maxHeight = 50;
		ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;
		Config decodeConfig = Bitmap.Config.ARGB_8888;
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};
		ImageRequest request = new ImageRequest(url, listener, maxWidth, maxHeight,
				scaleType, decodeConfig, errorListener) {
			@Override
			protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
				Map<String, String> headers = response.headers;
				String sessionid = headers.get("Set-Cookie");
				if (sessionid != null) {
					CommonRequest.JSESSIONID = sessionid.split(";")[0];
				}
				return super.parseNetworkResponse(response);
			}
		};
		
		queue.add(request);
	}

	@Override
	public void loginWithoutPwd(String token, final AsyncCallback callback) {
		String url = GlobalConsts.URL_USER_LOGIN_WITHOUT_PWD + "?token=" + token;
		CommonRequest request = new CommonRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getInt("code") == GlobalConsts.RESPONSE_CODE_SUCCESS) {
						JSONObject userObj = jsonObject.getJSONObject("user");
						TBookApplication app = TBookApplication.getContext();
						app.setCurrentUser(JSONParser.parseUser(userObj));
						callback.onSuccess(response);
					} else {
						callback.onError(response);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callback.onError(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		
		queue.add(request);

	}

}
