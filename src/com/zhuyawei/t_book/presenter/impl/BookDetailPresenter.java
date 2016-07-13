package com.zhuyawei.t_book.presenter.impl;

import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.model.ICartModel;
import com.zhuyawei.t_book.model.impl.CartModel;
import com.zhuyawei.t_book.presenter.IBookDetailPresenter;
import com.zhuyawei.t_book.view.IBookDetailView;

public class BookDetailPresenter implements IBookDetailPresenter {
	
	ICartModel model;
	IBookDetailView view;

	public BookDetailPresenter(IBookDetailView view) {
		this.view = view;
		model = new CartModel();
	}

	@Override
	public void addToCart(Book book) {
		if(model.addBook(book)) {
			view.addToCartSuccess();
		}
		else {
			view.addToCartFail("fail");
		}
		
	}

}
