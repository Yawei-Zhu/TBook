package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.User;

public interface IUserModel extends IModel {
	
	public void login(String loginname, String password, AsyncCallback callback);

	public void regist(User user, String code, AsyncCallback callback);

	public void getImageCode(AsyncCallback callback);

	public void loginWithoutPwd(String token, AsyncCallback callback);

}
