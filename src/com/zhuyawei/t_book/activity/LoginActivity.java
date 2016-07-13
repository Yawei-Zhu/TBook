package com.zhuyawei.t_book.activity;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.R.layout;
import com.zhuyawei.t_book.R.menu;
import com.zhuyawei.t_book.presenter.ILoginPresenter;
import com.zhuyawei.t_book.presenter.impl.LoginPresenter;
import com.zhuyawei.t_book.view.ILoginView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements ILoginView {
	
	@ViewInject(R.id.et_loginname)
	private EditText etLoginname;
	@ViewInject(R.id.et_password)
	private EditText etPassword;
	@ViewInject(R.id.bt_login)
	private Button btLogin;
	@ViewInject(R.id.tv_new_account)
	private TextView tvNewAccount;
	@ViewInject(R.id.tv_modify_password)
	private TextView tvModifyPassword;
	@ViewInject(R.id.iv_back)
	private ImageView ivBack;
	private ILoginPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		presenter = new LoginPresenter(this);
		
		x.view().inject(this);
		setListeners();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public void loginSuccess() {
		Toast.makeText(this,"µ«¬º≥…π¶",Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void loginFailed(String errorMessage) {
		Toast.makeText(this,"µ«¬º ß∞‹,"+errorMessage,Toast.LENGTH_SHORT).show();
	}
	
	private void setListeners() {
		View.OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.bt_login :
					String loginname = etLoginname.getText().toString();
					String password = etPassword.getText().toString();
					
					if(loginname.equals("") || password.equals("")){
						Toast.makeText(LoginActivity.this,"«Î ‰»Î’À∫≈ªÚ√‹¬Î",Toast.LENGTH_SHORT)
						.show();
						return;
					}
					
					presenter.login(loginname, password);
					break;
				case R.id.tv_new_account :
					Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
					LoginActivity.this.startActivity(i);
					break;
				case R.id.tv_modify_password :
					// TODO
					break;
				case R.id.iv_back :
					finish();
					break;
				}
			}
		};
		
		btLogin.setOnClickListener(listener);
		tvNewAccount.setOnClickListener(listener);
		tvModifyPassword.setOnClickListener(listener);
		ivBack.setOnClickListener(listener);
	}

}
