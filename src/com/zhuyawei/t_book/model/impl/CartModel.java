package com.zhuyawei.t_book.model.impl;

import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.entity.Cart;
import com.zhuyawei.t_book.model.ICartModel;

public class CartModel implements ICartModel {
	
	private Cart cart;
	

	public CartModel() {
		this.cart = TBookApplication.getContext().getCart();
	}

	@Override
	public boolean addBook(Book book) {
		return cart.buy(book);
	}

	@Override
	public void deleteBook(int bookId) {
		cart.deleteBook(bookId);
	}

	@Override
	public void modifyNum(int bookId, int num) {
		cart.modifyNum(bookId, num);
	}

	@Override
	public double getTotalPrice() {
		return cart.getTotalPrice();
	}

}
