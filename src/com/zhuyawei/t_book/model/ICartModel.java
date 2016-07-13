package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.Book;

public interface ICartModel extends IModel {

	/**
	 *添加图书
	 */
	public boolean addBook(Book book);

	/**
	 *删除图书
	 */
	public void deleteBook(int bookId);

	/**
	 *修改购物车中某一本书的数量
	 */
	public void modifyNum(int bookId, int num);

	/**
	 *获取总价格
	 */
	public double getTotalPrice();
	
}
