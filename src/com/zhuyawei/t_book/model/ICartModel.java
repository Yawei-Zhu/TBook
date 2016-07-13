package com.zhuyawei.t_book.model;

import com.zhuyawei.t_book.entity.Book;

public interface ICartModel extends IModel {

	/**
	 *���ͼ��
	 */
	public boolean addBook(Book book);

	/**
	 *ɾ��ͼ��
	 */
	public void deleteBook(int bookId);

	/**
	 *�޸Ĺ��ﳵ��ĳһ���������
	 */
	public void modifyNum(int bookId, int num);

	/**
	 *��ȡ�ܼ۸�
	 */
	public double getTotalPrice();
	
}
