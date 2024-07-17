package com.trading.service;

import java.util.List;

import com.trading.domain.OrderType;
import com.trading.modal.Coin;
import com.trading.modal.Order;
import com.trading.modal.OrderItem;
import com.trading.modal.User;

public interface OrderService {

	Order createOrder(User user, OrderItem orderItem, OrderType orderType);

	Order getOrderById(Long orderId) throws Exception;

	List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

	Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
