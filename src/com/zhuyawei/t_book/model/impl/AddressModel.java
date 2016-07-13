package com.zhuyawei.t_book.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.model.IAddressModel;
import com.zhuyawei.t_book.util.CommonRequest;
import com.zhuyawei.t_book.util.GlobalConsts;
import com.zhuyawei.t_book.util.JSONParser;

public class AddressModel implements IAddressModel {
	
	private RequestQueue queue;

	public AddressModel() {
		queue = Volley.newRequestQueue(TBookApplication.getContext());
	}

	@Override
	public void saveAddress(final Address address, final AsyncCallback callback) {
		int method = Request.Method.POST;
		String url= GlobalConsts.URL_SAVE_ADDRESS;
		Response.Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				callback.onSuccess(response);
			}
		};
		Response.ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onError(error);
			}
		};
		
		CommonRequest request = new CommonRequest(method, url, listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> resultMap = new HashMap<String, String>();
				resultMap.put("address.receiveName",address.getReceiveName());
				resultMap.put("address.full_address",address.getFull_address());
				resultMap.put("address.postalCode",address.getPostalCode());
				resultMap.put("address.mobile",address.getMobile());
				resultMap.put("address.phone",address.getPhone());
				return resultMap;
			}
		};
		queue.add(request);
	}

	@Override
	public void listAddress(final AsyncCallback callback) {
		String url = GlobalConsts.URL_LOAD_USER_ADDRESS;
		Response.Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject obj = new JSONObject(response);
					if(obj.getInt("code") == GlobalConsts.RESPONSE_CODE_SUCCESS) {
						JSONArray arr = obj.getJSONArray("data");
						List<Address> addresses = JSONParser.parseAddress(arr);
						callback.onSuccess(addresses);
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
			}
		};
		CommonRequest request = new CommonRequest(url, listener, errorListener);
		
		queue.add(request);
	}

	@Override
	public void setDefault(int id, final AsyncCallback callback) {
		String url = GlobalConsts.URL_SET_ADDRESS_DEFAULT + "?id=" + id;
		Response.Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					if(jsonObject.getInt("code")==GlobalConsts.RESPONSE_CODE_SUCCESS){
						callback.onSuccess(null);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		Response.ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		};
		CommonRequest request = new CommonRequest(url, listener, errorListener);
		queue.add(request);
	}

}
