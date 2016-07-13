package com.zhuyawei.t_book.view;

import java.util.List;

import com.zhuyawei.t_book.entity.Order;

public interface IOrderView extends IView {
	void setOrders(List<Order> orders);
}
