package com.zhuyawei.t_book.view;

import java.util.List;

import com.zhuyawei.t_book.entity.Address;

public interface IAddressView {
	/**
	 * 使保存地址的对话框消失
	 */
	public void dismissSaveAddressDialog();

	/**
	 * 设置地址数据源集合
	 * @param addresses
	 */
	public void setAddresses(List<Address> addresses);

	/**
	 * 设置适配器
	 */
	public void setAdapter();
}
