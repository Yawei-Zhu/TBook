package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.Address;

public interface IAddressModel extends IModel {
	/**
	 *添加地址
	 */
	public void saveAddress(Address address, AsyncCallback callback);

	/**
	 * 地址列表
	 */
	public void listAddress(AsyncCallback callback);

	/**
	 * 设置默认地址
	 */
	public void setDefault(int id, AsyncCallback callback);
	
}
