package com.zhuyawei.t_book.presenter;

import com.zhuyawei.t_book.entity.Book;

public interface IBookDetailPresenter extends IPresenter {

	/**
	 * ��ͼ����ӵ����ﳵ
	 */
	public void addToCart(Book book);
	
}
