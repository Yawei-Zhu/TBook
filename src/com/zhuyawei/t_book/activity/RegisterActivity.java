package com.zhuyawei.t_book.activity;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.R.layout;
import com.zhuyawei.t_book.R.menu;
import com.zhuyawei.t_book.entity.User;
import com.zhuyawei.t_book.presenter.IRegisterPresenter;
import com.zhuyawei.t_book.presenter.impl.RegisterPresenter;
import com.zhuyawei.t_book.view.CircleImageView;
import com.zhuyawei.t_book.view.IRegisterView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements IRegisterView {
	
	@ViewInject(R.id.iv_register_code)
	private ImageView ivCode;
	@ViewInject(R.id.iv_regiter_photo)
	private CircleImageView ivPhoto;
	@ViewInject(R.id.et_register_loginname)
	private EditText etLoginname;
	@ViewInject(R.id.et_register_password)
	private EditText etPassword;
	@ViewInject(R.id.et_register_realname)
	private EditText etRealname;
	@ViewInject(R.id.et_register_code)
	private EditText etCode;
	@ViewInject(R.id.bt_register)
	private Button btRegister;

	private IRegisterPresenter presenter;

	public RegisterActivity() {
		presenter = new RegisterPresenter(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		x.view().inject(this);
		presenter.loadImage();
		setListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public void registSuccess() {
		Toast.makeText(this, "×¢²á³É¹¦", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void registError(String errorMessage) {
		Toast.makeText(this, "×¢²áÊ§°Ü,"+errorMessage, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showCodeImage(Bitmap bitmap) {
		if(bitmap != null){
			ivCode.setImageBitmap(bitmap);
		}
	}
	
	public void back(View view) {
		finish();
	}
	
	private void setListeners() {
		View.OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.bt_register :
					User user = new User();
					user.setEmail(etLoginname.getText().toString());
					user.setPassword(etPassword.getText().toString());
					user.setNickname(etRealname.getText().toString());
					String code = etCode.getText().toString();
					presenter.regist(user, code);
					break;
				case R.id.iv_register_code :
					presenter.loadImage();
					break;
				}
			}
		};
		
		btRegister.setOnClickListener(listener);
		ivCode.setOnClickListener(listener);
		ivPhoto.setOnClickListener(listener);
	}

}
