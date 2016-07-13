package com.zhuyawei.t_book.presenter;

import com.zhuyawei.t_book.entity.Book;

public interface ICartPresenter extends IPresenter {

	/**
	 * ���ͼ��
	 */
	public boolean addBook(Book book);

	/**
	 * ɾ��ͼ��
	 */
	public void deleteBook(int bookId);

	/**
	 * �޸�ĳ�������
	 */
	public void modifyNum(int bookId, int num);

	/**
	 * �����ܼ�
	 */
	public void loadTotalPrice();

}
