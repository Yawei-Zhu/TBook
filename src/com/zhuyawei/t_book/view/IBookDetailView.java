package com.zhuyawei.t_book.view;

public interface IBookDetailView extends IView {

	/**
	 * ��ӹ��ﳵ�ɹ���ִ��
	 */
	public void addToCartSuccess();

	/**
	 * ��ӹ��ﳵʧ�ܺ�ִ��
	 * @param errorMessage ʧ��ԭ����Ϣ
	 */
	public void addToCartFail(String errorMessage);
	
	
}
