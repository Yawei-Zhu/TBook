package com.zhuyawei.t_book.fragment;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.activity.OrderInfoActivity;
import com.zhuyawei.t_book.adapter.CartItemAdapter;
import com.zhuyawei.t_book.entity.CartItem;
import com.zhuyawei.t_book.presenter.ICartPresenter;
import com.zhuyawei.t_book.presenter.impl.CartPresenter;
import com.zhuyawei.t_book.view.ICartView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CartFragment extends Fragment implements ICartView {
	
	@ViewInject(R.id.bt_edit)
	private Button btEdit;
	@ViewInject(R.id.lv_cart)
	private ListView lvCart;
	@ViewInject(R.id.tv_total)
	private TextView tvTotal;
	@ViewInject(R.id.bt_submit)
	private Button btSubmit;
	@ViewInject(R.id.tv_empty_cart)
	private TextView tvEmptyCart;
	private List<CartItem> items;
	private CartItemAdapter adapter;
	private ICartPresenter cartPresenter;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cart, null);
		
		cartPresenter = new CartPresenter(this);
		
		x.view().inject(this, view);
		items = TBookApplication.getContext().getCart().getItems();
		
		setListeners();
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setAdapter();
	}

	@Override
	public void updateTotalPrice(double price) {
		tvTotal.setText("£§" + price);
	}
	
	private void setListeners() {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.bt_edit :
					adapter.deleteToggle();
					break;
				case R.id.bt_submit :
					Intent intent = new Intent(getActivity(), OrderInfoActivity.class);
					startActivity(intent);
					break;
				}
			}
		};
		
		btEdit.setOnClickListener(listener);
		btSubmit.setOnClickListener(listener);
	}
	
	private void setAdapter() {
		adapter = new CartItemAdapter(getActivity(), items, lvCart);
		adapter.setPresenter(cartPresenter);
		lvCart.setAdapter(adapter);
		//º∆À„∫œº∆
		cartPresenter.loadTotalPrice();
	}
	
}
