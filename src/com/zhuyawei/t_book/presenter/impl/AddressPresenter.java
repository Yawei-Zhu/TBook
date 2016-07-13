package com.zhuyawei.t_book.presenter.impl;

import java.util.List;

import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.model.IAddressModel;
import com.zhuyawei.t_book.model.IModel;
import com.zhuyawei.t_book.model.impl.AddressModel;
import com.zhuyawei.t_book.presenter.IAddressPresenter;
import com.zhuyawei.t_book.view.IAddressView;

public class AddressPresenter implements  IAddressPresenter{

	private IAddressView view;
	private IAddressModel model;

	public AddressPresenter(IAddressView view) {
		this.view = view;
		model = new AddressModel();
	}

	@Override
	public void saveAddress(Address address) {
		model.saveAddress(address, new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.dismissSaveAddressDialog();
			}
			
			@Override
			public void onError(Object error) {
			}
		});
	}

	@Override
	public void listAddress() {
		model.listAddress(new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.setAddresses((List<Address>)success);
				view.setAdapter();
			}
			
			@Override
			public void onError(Object error) {
			}
		});
	}

	@Override
	public void setDefault(int id) {
		model.setDefault(id, new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				listAddress();
			}
			
			@Override
			public void onError(Object error) {
			}
		});
	}

}
