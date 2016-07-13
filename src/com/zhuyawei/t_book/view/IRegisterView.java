package com.zhuyawei.t_book.view;

import android.graphics.Bitmap;

public interface IRegisterView extends IView {
	
	/**
	 * 注册成功
	 */
	void registSuccess();

	/**
	 * 注册失败提示
	 * @param errorMessage
	 */
	void registError(String errorMessage);

	/**
	 * 显示验证码
	 * @param bitmap
	 */
	void showCodeImage(Bitmap bitmap);
	
}
