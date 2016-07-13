package com.zhuyawei.t_book.presenter;

public interface IOrderInfoPresenter extends IPresenter {

	/**
	 * 提交订单
	 */
	void submitOrder(int addressId, String cartInfo);

	/**
	 * 加载购物车信息
	 */
	void loadCartInfo();

	/**
	 * 加载默认地址
	 */
	void loadMyDefaultAddress();

}
