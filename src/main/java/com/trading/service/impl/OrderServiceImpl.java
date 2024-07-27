package com.trading.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.domain.OrderStatus;
import com.trading.domain.OrderType;
import com.trading.modal.Asset;
import com.trading.modal.Coin;
import com.trading.modal.Order;
import com.trading.modal.OrderItem;
import com.trading.modal.User;
import com.trading.repository.OrderItemRepository;
import com.trading.repository.OrderRepository;
import com.trading.service.AssetService;
import com.trading.service.OrderService;
import com.trading.service.WalletService;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private WalletService walletService;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private AssetService assetService;

	@Override
	public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
		double price = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();
		Order order = new Order();
		order.setUser(user);
		order.setOrderItem(orderItem);
		order.setOrderType(orderType);
		order.setPrice(BigDecimal.valueOf(price));
		order.setTimestamp(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		return orderRepository.save(order);
	}

	@Override
	public Order getOrderById(Long orderId) throws Exception {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new Exception("Order not found with the id:" + orderId));
	}

	@Override
	public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
		return orderRepository.findByUserId(userId);
	}

	private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice) {
		OrderItem orderItem = new OrderItem();
		orderItem.setCoin(coin);
		orderItem.setQuantity(quantity);
		orderItem.setBuyPrice(buyPrice);
		orderItem.setSellPrice(sellPrice);
		return orderItemRepository.save(orderItem);
	}

	@Transactional
	public Order buyAsset(Coin coin, double quantity, User user) throws Exception {
		if (quantity <= 0) {
			throw new Exception("Quantity should be greater than 0.");
		}

		double buyPrice = coin.getCurrentPrice();
		OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);
		Order createOrder = createOrder(user, orderItem, OrderType.BUY);
		orderItem.setOrder(createOrder);
		walletService.payOrderPayment(createOrder, user);

		createOrder.setStatus(OrderStatus.SUCCESS);
		createOrder.setOrderType(OrderType.BUY);
		Order savedOrder = orderRepository.save(createOrder);

		Asset oldAsset = assetService.findAssetByUserIdAndCoinId(savedOrder.getUser().getId(),
				savedOrder.getOrderItem().getCoin().getId());

		if (oldAsset == null) {
			assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
		} else {
			assetService.updateAsset(oldAsset.getId(), quantity);
		}
		return savedOrder;
	}

	@Transactional
	public Order sellAsset(Coin coin, double quantity, User user) throws Exception {
		if (quantity <= 0) {
			throw new Exception("Quantity should be greater than 0.");
		}

		double sellPrice = coin.getCurrentPrice();

		Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());
		double buyPrice = assetToSell.getBuyPrice();

		OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);
		Order createOrder = createOrder(user, orderItem, OrderType.SELL);
		orderItem.setOrder(createOrder);
		if (assetToSell.getQuantity() >= quantity) {
			createOrder.setStatus(OrderStatus.SUCCESS);
			createOrder.setOrderType(OrderType.SELL);
			Order savedOrder = orderRepository.save(createOrder);
			walletService.payOrderPayment(createOrder, user);
			Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
			if (updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1) {
				assetService.deleteAsset(updatedAsset.getId());
			}
			return savedOrder;
		}
		throw new Exception("Insufficient quantity to sell.");
	}

	@Override
	@Transactional
	public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception {
		if (orderType.equals(OrderType.BUY)) {
			return buyAsset(coin, quantity, user);
		} else if (orderType.equals(OrderType.SELL)) {
			return sellAsset(coin, quantity, user);
		}
		throw new Exception("Invalid order type");
	}

}
