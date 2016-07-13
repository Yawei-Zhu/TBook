package com.zhuyawei.t_book.presenter.impl;

import java.util.List;

import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.model.IModel;
import com.zhuyawei.t_book.model.IStoreModel;
import com.zhuyawei.t_book.model.impl.StoreModel;
import com.zhuyawei.t_book.presenter.IStorePresenter;
import com.zhuyawei.t_book.view.IStoreView;

/**
 * Created by Yawei.Zhu on 16/6/22.
 */
public class StorePresenter implements IStorePresenter {
	private IStoreView storeView;
	private IStoreModel storeModel;

	public StorePresenter(IStoreView storeView) {
		this.storeView = storeView;
		this.storeModel = new StoreModel();
	}

	@Override
	public void loadRecommendBooks() {
		storeModel.getRecommendList(new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object obj) {
				@SuppressWarnings("unchecked")
				List<Book> books = (List<Book>) obj;
				storeView.updateRecommendList(books);
			}

			@Override
			public void onError(Object error) {
			}
		});
	}

	@Override
	public void loadHotBooks() {
		storeModel.getHotList(new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object obj) {
				@SuppressWarnings("unchecked")
				List<Book> books = (List<Book>) obj;
				storeView.updateHotList(books);
			}
			@Override
			public void onError(Object error) {
			}
		});
	}

	@Override
	public void loadNewBooks() {
		storeModel.getNewList(new IModel.AsyncCallback() {
			@Override
			public void onSuccess(Object obj) {
				@SuppressWarnings("unchecked")
				List<Book> books = (List<Book>) obj;
				storeView.updateNewList(books);
			}

			@Override
			public void onError(Object error) {
			}
		});
	}
}
