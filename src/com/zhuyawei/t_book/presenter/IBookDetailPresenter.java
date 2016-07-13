package com.zhuyawei.t_book.presenter;

import com.zhuyawei.t_book.entity.Book;

public interface IBookDetailPresenter extends IPresenter {

	/**
	 * 把图书添加到购物车
	 */
	public void addToCart(Book book);
	
}
