package com.zhuyawei.t_book.view;

import android.graphics.Bitmap;

public interface IRegisterView extends IView {
	
	/**
	 * ע��ɹ�
	 */
	void registSuccess();

	/**
	 * ע��ʧ����ʾ
	 * @param errorMessage
	 */
	void registError(String errorMessage);

	/**
	 * ��ʾ��֤��
	 * @param bitmap
	 */
	void showCodeImage(Bitmap bitmap);
	
}
