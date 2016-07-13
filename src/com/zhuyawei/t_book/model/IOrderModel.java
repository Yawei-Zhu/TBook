package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.Cart;

public interface IOrderModel extends IModel {

	/**
	 * 提交订单
	 * @param callback
	 */
	void submitOrder(int addressId, String cartInfo, AsyncCallback callback);

	/**
	 * 加载我的购物车信息
	 * @return
	 */
	Cart loadMyCartInfo();

	/**
	 * 加载我的默认地址
	 * @param callback
	 */
	void loadMyDefaultAddress(AsyncCallback callback);
	
}
