package com.zhuyawei.t_book.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.util.GlobalConsts;

public class Cart implements Serializable {

	private List<CartItem> items = new ArrayList<CartItem>();

	public List<CartItem> getItems() {
		return items;
	}

	public boolean buy(Book book) {
		CartItem item;
		for(int i = 0 ; i < items.size(); i++) {
			item = items.get(i);
			if(book != null && book.equals(item.getBook())) {
				item.setCount(item.getCount() + 1);
				return true;
			}
		}

		item = new CartItem(book, 1); 
		items.add(item);

		saveCart();
		return true;
	}


	public void deleteBook(int bookId) {
		for(CartItem item : items) {
			if(bookId == item.getBook().getId()) {
				items.remove(item);

				saveCart();
				return;
			}
		}

	}

	public void modifyNum(int bookId, int num) {
		for(CartItem item : items) {
			if(item.getBook().getId() == bookId) {
				item.setCount(num);

				saveCart();
				return;
			}
		}
	}
	
	public int getNumberOfBooks() {
		int number = 0;
		for(CartItem item : items) {
			number += item.getCount();
		}
		return number ;
	}

	public double getTotalPrice() {
		double total = 0;
		for(CartItem item : items) {
			total += item.getBook().getDangPrice() * item.getCount();
		}

		return total;
	}

	public Cart readCart() {
		try {
			File file = new File(TBookApplication.getContext().getCacheDir(), GlobalConsts.CART_CACHE_FILE_NAME);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Cart cart = (Cart) ois.readObject();
			if(cart == null){
				return new Cart();
			}
			return cart;
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Cart();
	}

	private void saveCart() {
		try {
			File file = new File(TBookApplication.getContext().getCacheDir(), GlobalConsts.CART_CACHE_FILE_NAME);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(this);
			oos.flush();
			oos.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 1,1;2,3;4,1
	 */
	public String CartToString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < items.size(); i++) {
			CartItem c = items.get(i);
			sb.append(c.getBook().getId());
			sb.append("," + c.getCount() + ";");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

}
