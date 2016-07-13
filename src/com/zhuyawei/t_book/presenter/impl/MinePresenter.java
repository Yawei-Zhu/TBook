package com.zhuyawei.t_book.presenter.impl;

import com.zhuyawei.t_book.model.IModel.AsyncCallback;
import com.zhuyawei.t_book.model.IUserModel;
import com.zhuyawei.t_book.model.impl.UserModel;
import com.zhuyawei.t_book.presenter.IMinePresenter;
import com.zhuyawei.t_book.view.IMineView;

public class MinePresenter implements IMinePresenter {

	private IMineView view;
	private IUserModel model;

	public MinePresenter(IMineView view) {
		this.view = view;
		this.model = new UserModel();
	}

	@Override
	public void loginWithoutPwd(String token) {
		model.loginWithoutPwd(token, new AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.updataUserInfo();
			}
			
			@Override
			public void onError(Object error) {
			}
		});
	}

}
