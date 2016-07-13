package com.zhuyawei.t_book.activity;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.R.layout;
import com.zhuyawei.t_book.R.menu;
import com.zhuyawei.t_book.entity.Book;
import com.zhuyawei.t_book.presenter.IBookDetailPresenter;
import com.zhuyawei.t_book.presenter.impl.BookDetailPresenter;
import com.zhuyawei.t_book.util.BitmapUtils;
import com.zhuyawei.t_book.util.GlobalConsts;
import com.zhuyawei.t_book.view.IBookDetailView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailActivity extends Activity implements IBookDetailView {
	
	@ViewInject(R.id.iv_header_background)
	private ImageView ivBackground;
	@ViewInject(R.id.iv_book_pic)
	private ImageView ivPic;
	@ViewInject(R.id.tv_book_name)
	private TextView tvName;
	@ViewInject(R.id.tv_book_price)
	private TextView tvPrice;
	@ViewInject(R.id.tv_author)
	private TextView tvAuthor;
	@ViewInject(R.id.tv_date)
	private TextView tvDate;
	@ViewInject(R.id.tv_publish)
	private TextView tvPublish;
	@ViewInject(R.id.tv_category)
	private TextView tvCategory;
	@ViewInject(R.id.tv_description)
	private TextView tvDescription;
	@ViewInject(R.id.bt_add)
	private Button btAdd;
	private IBookDetailPresenter presenter;
	private Book book;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);
		
		x.view().inject(this);
		presenter = new BookDetailPresenter(this);
		
		Intent intent = getIntent();
		book = (Book) intent.getSerializableExtra("book");
		setBookInfo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_detail, menu);
		return true;
	}

	@Override
	public void addToCartSuccess() {
		Toast.makeText(this,"已添加到购物车",Toast.LENGTH_SHORT).show();
		btAdd.setText("已添加到购物车");
		btAdd.setEnabled(false);
	}

	@Override
	public void addToCartFail(String errorMessage) {
		Toast.makeText(this,"购物车添加失败:"+errorMessage,Toast.LENGTH_SHORT).show();
		btAdd.setEnabled(true);
	}
	
	/**
	 * 点击添加购物车后执行
	 * @param view
	 */
	public void addCart(View view){
		btAdd.setEnabled(false);
		presenter.addToCart(book);
	}

	private void setBookInfo() {
		tvAuthor.setText(book.getAuthor());
		tvCategory.setText(book.getCatalogue());
		tvDate.setText(book.getPublishedTime());
		tvPublish.setText(book.getPublishing());
		tvDescription.setText(book.getDescription());
		
		x.image().bind(ivPic, GlobalConsts.BASEURL + "productImages/" + book.getProduct_pic(), new Callback.CommonCallback<Drawable>() {
			@Override
			public void onSuccess(Drawable drawable) {
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				bitmap = BitmapUtils.createBlurBitmap(bitmap, 20);
				ivBackground.setImageBitmap(bitmap);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}
			@Override
			public void onCancelled(CancelledException cex) {
			}
			@Override
			public void onFinished() {
			}
		});
	}
	

}
