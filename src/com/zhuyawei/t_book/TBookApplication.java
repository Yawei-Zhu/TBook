package com.zhuyawei.t_book;

import org.xutils.x;

import com.zhuyawei.t_book.entity.Cart;
import com.zhuyawei.t_book.entity.User;
import com.zhuyawei.t_book.fragment.CartFragment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class TBookApplication extends Application {
	
	private static TBookApplication app;
	private Cart cart;
	private User currentUser;

	@Override
	public void onCreate() {
		app = this;
		x.Ext.init(app);
		
		cart = new Cart();
		cart = cart.readCart();
	}

	public static TBookApplication getContext() {
		return app;
	}

	public Cart getCart() {
		return cart;
	}
	
	public void setToken(String token) {
		SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("token", token);
		editor.commit();
	}
	
	public String getToken() {
		SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
		String token = pref.getString("token", "");
		return token;
	}
	
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public User getCurrentUser() {
		return currentUser;
	}


}
