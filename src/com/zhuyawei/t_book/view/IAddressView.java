package com.zhuyawei.t_book.view;

import java.util.List;

import com.zhuyawei.t_book.entity.Address;

public interface IAddressView {
	/**
	 * ʹ�����ַ�ĶԻ�����ʧ
	 */
	public void dismissSaveAddressDialog();

	/**
	 * ���õ�ַ����Դ����
	 * @param addresses
	 */
	public void setAddresses(List<Address> addresses);

	/**
	 * ����������
	 */
	public void setAdapter();
}
