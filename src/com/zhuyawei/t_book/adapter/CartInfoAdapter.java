package com.zhuyawei.t_book.adapter;

import java.util.List;

import org.xutils.x;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.entity.CartItem;
import com.zhuyawei.t_book.util.GlobalConsts;

public class CartInfoAdapter extends BaseAdapter<CartItem> {

	public CartInfoAdapter(Context context, List<CartItem> items) {
		super(context, items);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		CartItem item = getItem(position);
		ViewHolder holder;
		
		if(view == null) {
			holder = new ViewHolder();
			view = getInflater().inflate(R.layout.item_cartinfo_cartitem, null);
			view.setTag(holder);
			
			holder.ivPic = (ImageView) view.findViewById(R.id.iv_book_pic);
			holder.tvName = (TextView) view.findViewById(R.id.tv_book_name);
			holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
			holder.tvCount = (TextView) view.findViewById(R.id.tv_count);
			holder.tvTotal = (TextView) view.findViewById(R.id.tv_total_price);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		
		x.image().bind(holder.ivPic, GlobalConsts.BASEURL + "productImages/" + 
					item.getBook().getProduct_pic());
		
		holder.tvName.setText(item.getBook().getProductName());
		holder.tvPrice.setText("гд" + item.getBook().getDangPrice());
		holder.tvCount.setText("x" + item.getCount());
		holder.tvTotal.setText("гд" + item.getBook().getDangPrice() * item.getCount());
		
		return view;
	}
	
	class ViewHolder {
		ImageView ivPic;
		TextView tvName;
		TextView tvPrice;
		TextView tvCount;
		TextView tvTotal;
		
	}

}
