package com.zhuyawei.t_book.presenter;

import com.zhuyawei.t_book.entity.Address;

public interface IAddressPresenter extends IPresenter {

	/**
	 * �����ַ
	 */
	public void saveAddress(Address address);

	/**
	 * ���ʵ�ַ�б�
	 */
	public void listAddress();

	/**
	 * ����Ĭ�ϵ�ַ
	 */
	public void setDefault(int id);
}
