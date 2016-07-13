package com.zhuyawei.t_book.view;

import com.zhuyawei.t_book.entity.Book;

import java.util.List;

/**
 * Created by hanamingming on 16/2/23.
 */
@SuppressWarnings("DefaultFileTemplate")
public interface IStoreView extends IView {

	/**
	 * @param books
	 */
	void updateRecommendList(List<Book> books);

	/**
	 * @param books
	 */
	void updateHotList(List<Book> books);

	/**
	 * @param books
	 */
	void updateNewList(List<Book> books);

}
