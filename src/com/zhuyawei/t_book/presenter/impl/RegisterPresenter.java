package com.zhuyawei.t_book.presenter.impl;

import android.graphics.Bitmap;

import com.zhuyawei.t_book.entity.User;
import com.zhuyawei.t_book.model.IModel;
import com.zhuyawei.t_book.model.IUserModel;
import com.zhuyawei.t_book.model.impl.UserModel;
import com.zhuyawei.t_book.presenter.IRegisterPresenter;
import com.zhuyawei.t_book.view.IRegisterView;

public class RegisterPresenter implements IRegisterPresenter {

	private IRegisterView view;
	private IUserModel model;

	public RegisterPresenter(IRegisterView view) {
		this.view = view;
		this.model = new UserModel();
	}

	@Override
	public void loadImage() {
		model.getImageCode(new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.showCodeImage((Bitmap)success);
			}
			@Override
			public void onError(Object error) {
			}
		});
	}

	@Override
	public void regist(User user, String code) {
		model.regist(user, code, new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.registSuccess();
			}
			
			@Override
			public void onError(Object error) {
				view.registError((String)error);
			}
		});
	}

}
