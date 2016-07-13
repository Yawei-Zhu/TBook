package com.zhuyawei.t_book.presenter.impl;

import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.model.ICartModel;
import com.zhuyawei.t_book.model.impl.CartModel;
import com.zhuyawei.t_book.presenter.ICartPresenter;
import com.zhuyawei.t_book.view.ICartView;

public class CartPresenter implements ICartPresenter {

	private ICartView view;
	private ICartModel model;

	public CartPresenter(ICartView cartView) {
		this.view = cartView;
		this.model = new CartModel();
	}
	
	@Override
	public boolean addBook(Book book) {
		return model.addBook(book);
	}

	@Override
	public void deleteBook(int bookId) {
		model.deleteBook(bookId);
		view.updateTotalPrice(model.getTotalPrice());
	}

	@Override
	public void modifyNum(int bookId, int num) {
		model.modifyNum(bookId, num);
		view.updateTotalPrice(model.getTotalPrice());
	}

	@Override
	public void loadTotalPrice() {
		view.updateTotalPrice(model.getTotalPrice());
	}

}
