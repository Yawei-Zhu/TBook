package com.zhuyawei.t_book.view;

import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.entity.Cart;

public interface IOrderInfoView extends IView {
	
	/**
	 * �ύ����ʧ��
	 * @param errorMessage
	 */
	void submitOrderFail(String errorMessage);

	/**
	 * �ύ�����ɹ�
	 */
	void submitOrderSuccess();

	/**
	 * ���¹��ﳵ��Ϣ
	 * @param cart
	 */
	void setCartInfo(Cart cart);

	/**
	 * ����Ĭ�ϵ�ַ��Ϣ
	 * @param address
	 */
	void updateAddressInfo(Address address);

}
