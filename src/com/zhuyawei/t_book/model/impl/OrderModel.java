package com.zhuyawei.t_book.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.entity.Cart;
import com.zhuyawei.t_book.model.IOrderModel;
import com.zhuyawei.t_book.util.CommonRequest;
import com.zhuyawei.t_book.util.GlobalConsts;

public class OrderModel implements IOrderModel {

	private RequestQueue queue;

	public OrderModel() {
		queue = Volley.newRequestQueue(TBookApplication.getContext());
	}
	
	@Override
	public void submitOrder(final int addressId, final String cartInfo, final AsyncCallback callback) {
		int mothed = Request.Method.POST;
		String url = GlobalConsts.URL_SUBMIT_ORDER;
		
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject obj = new JSONObject(response);
					if (obj.getInt("code") == GlobalConsts.RESPONSE_CODE_SUCCESS) {
						callback.onSuccess(null);
					}
					else {
						callback.onError("下订单出错啦，重试下~");
					}
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		};
		
		CommonRequest request = new CommonRequest(mothed, url, listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				params.put("addressId", addressId + "");
				params.put("cartInfo", cartInfo);
				return params;
			}
		};
		queue.add(request);
	}

	@Override
	public Cart loadMyCartInfo() {
		return TBookApplication.getContext().getCart().readCart();
	}

	@Override
	public void loadMyDefaultAddress(final AsyncCallback callback) {
		
		String url = GlobalConsts.URL_LOAD_DEFAULT_ADDRESS;;
		Response.Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject obj = new JSONObject(response);
					if (obj.getInt("code") == GlobalConsts.RESPONSE_CODE_SUCCESS) {
						obj = obj.getJSONObject("data");
						Address address = new Address(obj.getInt("id"),
										obj.getString("receiveName"),
										obj.getString("full_address"),
										obj.getString("postalCode"),
										obj.getString("mobile"),
										obj.getString("phone"));
						callback.onSuccess(address);
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
				callback.onError("默认地址加载失败");
			}
		};
		CommonRequest request = new CommonRequest(url, listener, errorListener);
		
		queue.add(request);
	}

}
