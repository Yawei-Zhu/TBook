package com.zhuyawei.t_book.view;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.entity.Address;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class AddressDialog extends Dialog {

	private Context context;
	private Callback callback;
	private EditText etName;
	private EditText etAddress;
	private EditText etCode;
	private EditText etPhone;
	private EditText etTel;
	private Button btSubmit;

	public AddressDialog(Context context, Callback callback) {
		super(context);
		this.context = context;
		this.callback = callback;
		
	}

	@Override
	public void show() {
		super.show();
		
		Window window = getWindow();
		window.setBackgroundDrawable(new ColorDrawable(0x00000000));
		View dialogView = View.inflate(context, R.layout.dialog_add_address, null);
		window.setContentView(dialogView);
		
		setViews(dialogView);
		setListeners();
	}




	private void setViews(View view) {
		etName = (EditText) view.findViewById(R.id.et_receive_name);
		etAddress = (EditText) view.findViewById(R.id.et_receive_address);
		etCode = (EditText) view.findViewById(R.id.et_code);
		etPhone = (EditText) view.findViewById(R.id.et_phone);
		etTel = (EditText) view.findViewById(R.id.et_tel);
		btSubmit = (Button) view.findViewById(R.id.bt_address_submit);
	}
	
	
	private void setListeners() {
		btSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Address address = new Address();
				address.setReceiveName(etName.getText().toString());
				address.setFull_address(etAddress.getText().toString());
				address.setMobile(etPhone.getText().toString());
				address.setPostalCode(etCode.getText().toString());
				address.setPhone(etTel.getText().toString());
				callback.onSubmit(address);
			}
		});
	}




	public interface Callback {
		void onSubmit(Address address);
	}

}
