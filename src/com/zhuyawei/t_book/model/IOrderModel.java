package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.Cart;

public interface IOrderModel extends IModel {

	/**
	 * �ύ����
	 * @param callback
	 */
	void submitOrder(int addressId, String cartInfo, AsyncCallback callback);

	/**
	 * �����ҵĹ��ﳵ��Ϣ
	 * @return
	 */
	Cart loadMyCartInfo();

	/**
	 * �����ҵ�Ĭ�ϵ�ַ
	 * @param callback
	 */
	void loadMyDefaultAddress(AsyncCallback callback);
	
}
