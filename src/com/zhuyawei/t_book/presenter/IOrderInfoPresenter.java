package com.zhuyawei.t_book.presenter;

public interface IOrderInfoPresenter extends IPresenter {

	/**
	 * �ύ����
	 */
	void submitOrder(int addressId, String cartInfo);

	/**
	 * ���ع��ﳵ��Ϣ
	 */
	void loadCartInfo();

	/**
	 * ����Ĭ�ϵ�ַ
	 */
	void loadMyDefaultAddress();

}
