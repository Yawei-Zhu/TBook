package com.zhuyawei.t_book.activity;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.R.layout;
import com.zhuyawei.t_book.R.menu;
import com.zhuyawei.t_book.adapter.CartInfoAdapter;
import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.entity.Cart;
import com.zhuyawei.t_book.presenter.IOrderInfoPresenter;
import com.zhuyawei.t_book.presenter.impl.OrderInfoPresenter;
import com.zhuyawei.t_book.view.IOrderInfoView;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderInfoActivity extends Activity implements IOrderInfoView {
	
	@ViewInject(R.id.tv_receive_address)
	private TextView tvAddress; 
	@ViewInject(R.id.tv_total_desc)
	private TextView tvTotalDesc; 
	@ViewInject(R.id.lv_cart)
	private ListView lvCart; 
	@ViewInject(R.id.bt_submit_order)
	private Button btSubmit; 
	
	private IOrderInfoPresenter presenter;
	private Cart cart;
	private Address address;
	private CartInfoAdapter adapter;

	public OrderInfoActivity() {
		presenter = new OrderInfoPresenter(this);
	}
	
	@Event(value = R.id.bt_submit_order, type = View.OnClickListener.class)
	private void submitOrder(View view){
		presenter.submitOrder(
				address.getId(),
				cart.CartToString());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_info);
		
		x.view().inject(this);
		presenter.loadMyDefaultAddress();
		presenter.loadCartInfo();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_info, menu);
		return true;
	}

	@Override
	public void submitOrderFail(String errorMessage) {
		Toast.makeText(this, "订单提交失败:"+errorMessage, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void submitOrderSuccess() {
		Toast.makeText(this, "订单提交完成", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void setCartInfo(Cart cart) {
		this.cart = cart;
		String html="共<font color='red'>" + cart.getNumberOfBooks() + "</font>件，" +
				"总金额：<font color='red'>￥" + cart.getTotalPrice() + "</font>";
		tvTotalDesc.setText(Html.fromHtml(html));
		
		setAdapter();
	}

	@Override
	public void updateAddressInfo(Address address) {
		this.address = address;
		String source = "收货人姓名：<font color='red'>" + address.getReceiveName() +
				"</font><br/>收货人电话：<font color='red'>" + address.getPhone() + 
				"</font><br/>收货人地址：<font color='red'>" + address.getFull_address() + "</font>";
		tvAddress.setText(Html.fromHtml(source));
	}

	private void setAdapter() {
		adapter = new CartInfoAdapter(this, cart.getItems());
		lvCart.setAdapter(adapter);
	}

}
