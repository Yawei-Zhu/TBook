package com.zhuyawei.t_book.presenter;

import com.zhuyawei.t_book.entity.User;

public interface IRegisterPresenter extends IPresenter {
	
	/**
	 *������֤��
	 */
	public void loadImage();

	/**
	 *ע��
	 */
	public void regist(User user, String code);

}
