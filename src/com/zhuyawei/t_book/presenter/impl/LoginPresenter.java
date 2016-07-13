package com.zhuyawei.t_book.presenter.impl;

import com.zhuyawei.t_book.model.IModel;
import com.zhuyawei.t_book.model.IUserModel;
import com.zhuyawei.t_book.model.impl.UserModel;
import com.zhuyawei.t_book.presenter.ILoginPresenter;
import com.zhuyawei.t_book.view.ILoginView;

public class LoginPresenter implements ILoginPresenter{

	private ILoginView view;
	private IUserModel model;

	public LoginPresenter(ILoginView view) {
		this.view = view;
		this.model = new UserModel();
	}

	@Override
	public void login(String loginname, String password) {
		model.login(loginname, password, new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.loginSuccess();
			}
			@Override
			public void onError(Object error) {
				view.loginFailed((String)error);
			}
		});
	}

}
