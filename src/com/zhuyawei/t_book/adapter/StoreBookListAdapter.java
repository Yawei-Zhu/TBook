package com.zhuyawei.t_book.adapter;

import java.util.List;

import org.xutils.x;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.util.GlobalConsts;

public class StoreBookListAdapter extends BaseAdapter<Book> {

	public StoreBookListAdapter(Context context,  List<Book> books) {
		super(context, books);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		Book book = getItem(position);
		
		if(view == null) {
			view = getInflater().inflate(R.layout.item_store_book, null);
			holder = new ViewHolder();
			view.setTag(holder);
			
			holder.ivPic = (ImageView) view.findViewById(R.id.iv_picture);
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.tvName.setText(book.getProductName());
		
		x.image().bind(holder.ivPic, GlobalConsts.BASEURL + "productImages/" + book.getProduct_pic());
		
		return view;
	}
	
	class ViewHolder{
		ImageView ivPic;
		TextView tvName;
	}

}
