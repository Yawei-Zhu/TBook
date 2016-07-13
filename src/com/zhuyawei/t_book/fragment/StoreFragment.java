package com.zhuyawei.t_book.fragment;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.activity.BookDetailActivity;
import com.zhuyawei.t_book.activity.OrderInfoActivity;
import com.zhuyawei.t_book.adapter.BaseAdapter;
import com.zhuyawei.t_book.adapter.StoreBookListAdapter;
import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.presenter.IStorePresenter;
import com.zhuyawei.t_book.presenter.impl.StorePresenter;
import com.zhuyawei.t_book.view.IStoreView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

public class StoreFragment extends Fragment implements IStoreView {
	
	@ViewInject(R.id.gv_recommend)
	private GridView gvRecommend;
	@ViewInject(R.id.et_search)
	private EditText etSearch;
	@ViewInject(R.id.gv_new)
	private GridView gvNew;
	@ViewInject(R.id.gv_hot)
	private GridView gvHot;
	
	private IStorePresenter storePresenter;
	
	private List<Book> recommendBooks;
	private StoreBookListAdapter recommendAdapter;

	private List<Book> hotBooks;
	private StoreBookListAdapter hotAdapter;

	private List<Book> newBooks;
	private StoreBookListAdapter newAdapter;
	
	public StoreFragment() {
		storePresenter = new StorePresenter(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_store, null);
		
		x.view().inject(this, view);
		
		storePresenter.loadRecommendBooks();
		storePresenter.loadHotBooks();
		storePresenter.loadNewBooks();
		
		setListeners();
		
		return view;
	}

	@Override
	public void updateRecommendList(List<Book> books) {
		recommendBooks = books;
		recommendAdapter = new StoreBookListAdapter(getActivity(), recommendBooks);
		gvRecommend.setAdapter(recommendAdapter);
	}

	@Override
	public void updateHotList(List<Book> books) {
		hotBooks = books;
		hotAdapter = new StoreBookListAdapter(getActivity(), hotBooks);
		gvHot.setAdapter(hotAdapter);
	}

	@Override
	public void updateNewList(List<Book> books) {
		newBooks = books;
		newAdapter = new StoreBookListAdapter(getActivity(), newBooks);
		gvNew.setAdapter(newAdapter);
	}
	
	private void setListeners() {
		AdapterView.OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StoreBookListAdapter adapter =  (StoreBookListAdapter) parent.getAdapter();
				Book book = adapter.getItem(position);
				Intent i = new Intent(getActivity(), BookDetailActivity.class);
				i.putExtra("book", book);
				startActivity(i);
			}
		};
		
		gvRecommend.setOnItemClickListener(listener);
		gvHot.setOnItemClickListener(listener);
		gvNew.setOnItemClickListener(listener);
	}

}
