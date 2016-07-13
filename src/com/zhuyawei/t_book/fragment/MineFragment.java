package com.zhuyawei.t_book.fragment;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.TBookApplication;
import com.zhuyawei.t_book.activity.AddressActivity;
import com.zhuyawei.t_book.activity.LoginActivity;
import com.zhuyawei.t_book.activity.OrderActivity;
import com.zhuyawei.t_book.entity.User;
import com.zhuyawei.t_book.presenter.IMinePresenter;
import com.zhuyawei.t_book.presenter.impl.MinePresenter;
import com.zhuyawei.t_book.view.IMineView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MineFragment extends Fragment implements IMineView {

	private static final int REQUEST_CODE_LOGIN_USER = 1;

	@ViewInject(R.id.rl_mine_header)
	private RelativeLayout mineHeader;
	@ViewInject(R.id.rl_item_shoucang)
	private RelativeLayout itemShoucang;
	@ViewInject(R.id.rl_item_address)
	private RelativeLayout itemAddress;
	@ViewInject(R.id.rl_item_order)
	private RelativeLayout itemOrder;
	@ViewInject(R.id.rl_item_settings)
	private RelativeLayout itemSettings;
	@ViewInject(R.id.rl_item_exit)
	private RelativeLayout itemExit;
	@ViewInject(R.id.tv_nickname)
	private TextView tvNickname;
	@ViewInject(R.id.iv_photo)
	private ImageView ivPhoto;
	private IMinePresenter presenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mine, null);
		presenter = new MinePresenter(this);
		x.view().inject(this, view);
		setListeners();
		//×Ô¶¯µÇÂ¼
		String token = TBookApplication.getContext().getToken();
		if(token != null && !token.equals("")) {
			presenter.loginWithoutPwd(token);
		}
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_LOGIN_USER) {
			if(resultCode == Activity.RESULT_OK) {
				updataUserInfo();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void updataUserInfo() {
		User user = TBookApplication.getContext().getCurrentUser();
		String nickname=user.getNickname();
		tvNickname.setText(nickname);
	}

	public void slide(float v) {
		mineHeader.setAlpha(v);
	}

	private void setListeners() {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()){
				case R.id.rl_item_shoucang:
					break;
				case R.id.rl_item_address:
					Intent intent = new Intent(getActivity(), AddressActivity.class);
					startActivityForResult(intent, REQUEST_CODE_LOGIN_USER);
					break;
				case R.id.rl_item_order:
					intent = new Intent(getActivity(), OrderActivity.class);
					startActivity(intent);
					break;
				case R.id.rl_item_settings:
					break;
				case R.id.rl_item_exit:
					break;
				case R.id.iv_photo:
					Intent i = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(i, REQUEST_CODE_LOGIN_USER);
					break;
				}
			}
		};
		itemShoucang.setOnClickListener(listener);
		itemAddress.setOnClickListener(listener);
		itemExit.setOnClickListener(listener);
		itemOrder.setOnClickListener(listener);
		itemSettings.setOnClickListener(listener);
		ivPhoto.setOnClickListener(listener);
	}

}
