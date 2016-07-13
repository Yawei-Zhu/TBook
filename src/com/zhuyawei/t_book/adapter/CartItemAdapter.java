package com.zhuyawei.t_book.adapter;

import java.util.List;

import org.xutils.x;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.entity.CartItem;
import com.zhuyawei.t_book.presenter.ICartPresenter;
import com.zhuyawei.t_book.util.GlobalConsts;

public class CartItemAdapter extends BaseAdapter<CartItem> {

	private ListView lvCart;
	public boolean show = false;
	private ICartPresenter presenter;

	public CartItemAdapter(Context context, List<CartItem> items, ListView lvCart) {
		super(context, items);
		this.lvCart = lvCart;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		CartItem item = getItem(position);
		ViewHolder holder;
		
		if(view == null) {
			view = getInflater().inflate(R.layout.item_cart_cartitem, null);
			holder = new ViewHolder();
			view.setTag(holder);
			
			holder.ivBookPic = (ImageView) view.findViewById(R.id.iv_book_pic);
			holder.tvBookName = (TextView) view.findViewById(R.id.tv_book_name);
			holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
			holder.tvCount = (TextView) view.findViewById(R.id.tv_count);
			holder.ivm = (ImageView) view.findViewById(R.id.iv_m);
			holder.ivp = (ImageView) view.findViewById(R.id.iv_p);
			holder.tvNum = (TextView) view.findViewById(R.id.tv_num);
			holder.ivDel = (ImageView) view.findViewById(R.id.iv_del);
			
		}
		else {
			holder = (ViewHolder) view.getTag();
		}
		
		String picPath = GlobalConsts.BASEURL + "productImages/" + item.getBook().getProduct_pic();
		x.image().bind(holder.ivBookPic, picPath);
		
		holder.tvBookName.setText(item.getBook().getProductName());
		holder.tvPrice.setText("£¤" + item.getBook().getDangPrice());
		holder.tvCount.setText("x" + item.getCount());
		holder.tvCount.setTag("tvCount" + position);
		holder.tvNum.setText(item.getCount() + "");
		holder.tvNum.setTag("tvNum" + position);
		holder.ivDel.setTag("ivDel" + position);
		
		if(!show) {
			holder.ivDel.setScaleX(0);
			holder.ivDel.setScaleY(0);
		}
		else {
			holder.ivDel.setScaleX(1);
			holder.ivDel.setScaleY(1);
		}
		
		holder.ivm.setOnClickListener(new ModifyNumListener(position, ModifyNumListener.BUTTON_MINUS));
		holder.ivp.setOnClickListener(new ModifyNumListener(position, ModifyNumListener.BUTTON_PLUS));
		holder.ivDel.setOnClickListener(new DelItemListener(position));
		
		return view;
	}

	public void deleteToggle() {
		int maxPosition = getCount() - 1;
		if (show) { //Á¢¼´Òþ²Ø
			for (int i = 0; i <= maxPosition; i++) {
				final ImageView ivDel = (ImageView) lvCart.findViewWithTag("ivDel" + i);
				ObjectAnimator anim = ObjectAnimator.ofFloat(ivDel, "abc", 1f, 0f);
				anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						float val = (Float) valueAnimator.getAnimatedValue();
						ivDel.setScaleX(val);
						ivDel.setScaleY(val);
					}
				});
				anim.setDuration(500);
				anim.start();
			}
			show = false;
		} 
		else { //Á¢¼´ÏÔÊ¾
			for (int i = 0; i <= maxPosition; i++) {
				final ImageView ivDel = (ImageView) lvCart.findViewWithTag("ivDel" + i);
				ObjectAnimator anim = ObjectAnimator.ofFloat(ivDel, "abc", 0f, 1f);
				anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator) {
						float val = (Float) valueAnimator.getAnimatedValue();
						ivDel.setScaleX(val);
						ivDel.setScaleY(val);
					}
				});
				anim.setDuration(500);
				anim.start();
			}
			show = true;
		}
	}

	public void setPresenter(ICartPresenter cartPresenter) {
		presenter = cartPresenter;
	}
	
	class ViewHolder {
		ImageView ivBookPic;
		TextView tvBookName;
		TextView tvPrice;
		TextView tvCount;
		ImageView ivm;
		ImageView ivp;
		TextView tvNum;
		ImageView ivDel;
	}
	
	class ModifyNumListener implements View.OnClickListener {
		public static final int BUTTON_PLUS = 1;
		public static final int BUTTON_MINUS = 2;

		private int position;
		private int type;

		ModifyNumListener(int position, int type) {
			this.position = position;
			this.type = type;
		}

		@Override
		public void onClick(View view) {
			TextView tvNum = (TextView) lvCart.findViewWithTag("tvNum" + position);
			TextView tvCount = (TextView) lvCart.findViewWithTag("tvCount" + position);
			int number = Integer.parseInt(tvNum.getText().toString());
			switch (type) {
				case BUTTON_PLUS:
					number++;
					tvNum.setText(number+"");
					tvCount.setText("x" + number);
					break;
				case BUTTON_MINUS:
					number = number==1?number:number-1;
					tvNum.setText(number+"");
					tvCount.setText("x" + number);
					break;
			}
			presenter.modifyNum(getItem(position).getBook().getId(), number);
		}
	}
	
	class DelItemListener implements View.OnClickListener{
		private int position;
		public DelItemListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			presenter.deleteBook(getItem(position).getBook().getId());
			CartItemAdapter.this.notifyDataSetChanged();
		}
	}

}
