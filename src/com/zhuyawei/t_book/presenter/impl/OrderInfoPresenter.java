package com.zhuyawei.t_book.presenter.impl;

import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.model.IModel;
import com.zhuyawei.t_book.model.IOrderModel;
import com.zhuyawei.t_book.model.impl.OrderModel;
import com.zhuyawei.t_book.presenter.IOrderInfoPresenter;
import com.zhuyawei.t_book.view.IOrderInfoView;

public class OrderInfoPresenter implements IOrderInfoPresenter{
	
	private IOrderInfoView view;
	private IOrderModel model;

	public OrderInfoPresenter(IOrderInfoView view) {
		this.view = view;
		model = new OrderModel();
	}

	@Override
	public void submitOrder(int addressId, String cartInfo) {
		model.submitOrder(addressId, cartInfo, new IModel.AsyncCallback() {
			public void onSuccess(Object success) {
				view.submitOrderSuccess();
			}
			public void onError(Object error) {
				view.submitOrderFail(error.toString());
			}
		});
	}

	@Override
	public void loadCartInfo() {
		view.setCartInfo(model.loadMyCartInfo());
	}

	@Override
	public void loadMyDefaultAddress() {
		model.loadMyDefaultAddress(new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object success) {
				view.updateAddressInfo((Address)success);
			}
			
			@Override
			public void onError(Object error) {
			}
		});
	}

}
