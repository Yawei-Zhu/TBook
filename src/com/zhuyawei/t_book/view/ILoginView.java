package com.zhuyawei.t_book.view;

public interface ILoginView extends IView {
	
	/**
	 * ��¼�ɹ���ʾ
	 */
	public void loginSuccess();


	/**
	 * ��¼ʧ����ʾ  ���Ҹ���ʧ��ԭ��
	 * @param errorMessage
	 */
	public void loginFailed(String errorMessage);

}
