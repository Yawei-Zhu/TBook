package com.zhuyawei.t_book.activity;

import java.util.ArrayList;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.zhuyawei.t_book.R;
import com.zhuyawei.t_book.fragment.CartFragment;
import com.zhuyawei.t_book.fragment.MineFragment;
import com.zhuyawei.t_book.fragment.StoreFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends FragmentActivity {
	
	@ViewInject(R.id.vp)
	private ViewPager mViewPager;
	@ViewInject(R.id.rg_tab)
	private RadioGroup mRadioGroup;
	@ViewInject(R.id.rb_store)
	private RadioButton tabStore;
	@ViewInject(R.id.rb_cart)
	private RadioButton tabCart;
	@ViewInject(R.id.rb_mine)
	private RadioButton tabMine;
	private ArrayList<Fragment> fragments;
	private MainPagerAdapter pagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		x.view().inject(this);
		setPagerAdapter();
		setListeners();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	private void setPagerAdapter() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new StoreFragment());
		fragments.add(new CartFragment());
		fragments.add(new MineFragment());
		
		pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
	}
	
	private void setListeners() {
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.rb_store :
					mViewPager.setCurrentItem(0);
					break;
				case R.id.rb_cart :
					mViewPager.setCurrentItem(1);
					break;
				case R.id.rb_mine :
					mViewPager.setCurrentItem(2);
					break;
				}
			}
		});
		
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				switch(position) {
				case 0 :
					tabStore.setChecked(true);
					break;
				case 1 :
					tabCart.setChecked(true);
					break;
				case 2 :
					tabMine.setChecked(true);
					break;
				}
			}
			@Override
			public void onPageScrolled(int i1, float v, int i2) {
				if(v!=0){ //当前是第3页
					//设置第三个fragment header的透明度
					MineFragment fragment = (MineFragment) fragments.get(2);
					fragment.slide(v);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	class MainPagerAdapter extends FragmentPagerAdapter {

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

	}
 
}
