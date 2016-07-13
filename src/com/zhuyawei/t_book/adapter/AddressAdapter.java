package com.zhuyawei.t_book.adapter;

import java.util.List;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.entity.Address;
import com.zhuyawei.t_book.presenter.IAddressPresenter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class AddressAdapter extends BaseAdapter<Address> {

	private IAddressPresenter presenter;

	public AddressAdapter(Context context, List<Address> addresses) {
		super(context, addresses);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Address address = getItem(position);
		ViewHolder holder;
		
		if(view == null) {
			view = getInflater().inflate(R.layout.item_address, null);
			holder = new ViewHolder();
			view.setTag(holder);
			
			holder.tvName = (TextView) view.findViewById(R.id.tv_receive_name);
			holder.tvNumber = (TextView) view.findViewById(R.id.tv_phone);
			holder.tvAddress = (TextView) view.findViewById(R.id.tv_receive_address);
			holder.rbDefault = (RadioButton) view.findViewById(R.id.rb_default);
			holder.rbEdit = (RadioButton) view.findViewById(R.id.rb_edit);
			holder.rbDelete = (RadioButton) view.findViewById(R.id.rb_delete);
			
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.tvName.setText(address.getReceiveName());
		holder.tvNumber.setText(address.getMobile());
		holder.tvAddress.setText(address.getFull_address());
		holder.rbDefault.setChecked(address.getIs_default() != 0);
		
		AddressAdapterListener listener = new AddressAdapterListener(position);
		holder.rbDefault.setOnCheckedChangeListener(listener);
		holder.rbEdit.setOnCheckedChangeListener(listener);
		holder.rbDelete.setOnCheckedChangeListener(listener);
		
		return view;
	}

	public void setPresenter(IAddressPresenter presenter) {
		this.presenter = presenter;
	}
	
	class ViewHolder {
		TextView tvName;
		TextView tvNumber;
		TextView tvAddress;
		RadioButton rbDefault;
		RadioButton rbEdit;
		RadioButton rbDelete;
	}
	
	class AddressAdapterListener implements CompoundButton.OnCheckedChangeListener {
		private int position;

		AddressAdapterListener(int position) {
			this.position = position;
		}
		
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			if(b){
				Address address = getItem(position);
				int id = address.getId();
				presenter.setDefault(id);
			}
		}
	}

}
