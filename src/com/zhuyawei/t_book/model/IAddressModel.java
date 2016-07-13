package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.Address;

public interface IAddressModel extends IModel {
	/**
	 *��ӵ�ַ
	 */
	public void saveAddress(Address address, AsyncCallback callback);

	/**
	 * ��ַ�б�
	 */
	public void listAddress(AsyncCallback callback);

	/**
	 * ����Ĭ�ϵ�ַ
	 */
	public void setDefault(int id, AsyncCallback callback);
	
}
