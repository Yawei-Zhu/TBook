package com.zhuyawei.t_book.view;

public interface ILoginView extends IView {
	
	/**
	 * 登录成功提示
	 */
	public void loginSuccess();


	/**
	 * 登录失败提示  并且附带失败原因
	 * @param errorMessage
	 */
	public void loginFailed(String errorMessage);

}
