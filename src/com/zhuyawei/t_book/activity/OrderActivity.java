package com.zhuyawei.t_book.activity;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.R.layout;
import com.zhuyawei.t_book.R.menu;
import com.zhuyawei.t_book.adapter.OrderAdapter;
import com.zhuyawei.t_book.entity.Order;
import com.zhuyawei.t_book.view.IOrderView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class OrderActivity extends Activity implements IOrderView {
	
	@ViewInject(R.id.lv_orders)
	private ListView lvOrders;
	private List<Order> orders;
	private OrderAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		
		x.view().inject(this);
		setListeners();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order, menu);
		return true;
	}
	
	@Override
	public void setOrders(List<Order> orders) {
		if(this.orders == null) {
			this.orders = orders;
			adapter = new OrderAdapter(this, orders);
			lvOrders.setAdapter(adapter);
		}
		else {
			this.orders.clear();
			this.orders.addAll(orders);
			if(adapter == null) {
				adapter = new OrderAdapter(this, orders);
				lvOrders.setAdapter(adapter);
			}
			else {
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	public void back(View view) {
		finish();
	}
	
	private void setListeners() {
		lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
	}

}
