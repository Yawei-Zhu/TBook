package com.zhuyawei.t_book.activity;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.R.layout;
import com.zhuyawei.t_book.R.menu;
import com.zhuyawei.t_book.adapter.AddressAdapter;
import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.presenter.IAddressPresenter;
import com.zhuyawei.t_book.presenter.impl.AddressPresenter;
import com.zhuyawei.t_book.view.AddressDialog;
import com.zhuyawei.t_book.view.IAddressView;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class AddressActivity extends Activity implements IAddressView {
	
	@ViewInject(R.id.lv_addresses)
	private ListView lvAddresses;
	private IAddressPresenter presenter;
	private Dialog dialog;
	private List<Address> addresses;
	private AddressAdapter addressAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		
		x.view().inject(this);
		presenter = new AddressPresenter(this);
		presenter.listAddress();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.address, menu);
		return true;
	}

	@Override
	public void dismissSaveAddressDialog() {
		Toast.makeText(this,"地址保存成功",Toast.LENGTH_SHORT).show();
		dialog.dismiss();
		presenter.listAddress();
	}

	@Override
	public void setAddresses(List<Address> addresses) {
		if(this.addresses == null) {
			this.addresses = addresses;
		}
		else {
			this.addresses.clear();
			this.addresses.addAll(addresses);
		}
	}

	@Override
	public void setAdapter() {
		if(addressAdapter == null){
			addressAdapter = new AddressAdapter(this, addresses);
			addressAdapter.setPresenter(presenter);
			lvAddresses.setAdapter(addressAdapter);
			return;
		}
		else {
			addressAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 添加监听
	 *
	 * @param view
	 */
	public void doClick(View view) {
		switch (view.getId()) {
			case R.id.bt_add_address:
				showAddAddressDialog();
				break;
		}
	}

	/**
	 * 弹出添加地址对话框
	 */
	private void showAddAddressDialog() {
		dialog = new AddressDialog(this, new AddressDialog.Callback() {
			@Override
			public void onSubmit(Address address) {
				presenter.saveAddress(address);
			}
		});
		dialog.show();
	}

}
