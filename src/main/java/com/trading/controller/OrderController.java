package com.trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trading.domain.OrderType;
import com.trading.modal.Coin;
import com.trading.modal.Order;
import com.trading.modal.User;
import com.trading.pojo.request.CreateOrderRequest;
import com.trading.service.CoinService;
import com.trading.service.OrderService;
import com.trading.service.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private CoinService coinService;

	@PostMapping("/pay")
	public ResponseEntity<Order> payOrderPayment(@RequestHeader("Authorization") String jwt,
			@RequestBody CreateOrderRequest request) throws Exception {

		User user = userService.findUserByJwt(jwt);
		Coin coin = coinService.findById(request.getCoinId());
		System.out.println("payOrderPayment||coin:"+coin);
		Order order = orderService.processOrder(coin, request.getQuantity(), request.getOrderType(), user);
		System.out.println("payOrderPayment||order:"+order);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrderById(@RequestHeader("Authorization") String jwt,
			@PathVariable Long orderId) throws Exception {

		User user = userService.findUserByJwt(jwt);
		Order order = orderService.getOrderById(orderId);

		if (order.getUser().getId().equals(user.getId())) {
			return ResponseEntity.ok(order);
		} else {
			throw new Exception("You don't have access.");
		}
	}

	@GetMapping()
	public ResponseEntity<List<Order>> getAllOrdersForUser(
			@RequestHeader("Authorization") String jwt,
			@RequestParam(required = false) OrderType orderType, 
			@RequestParam(required = false) String assetSymbol) throws Exception {
		
		User user = userService.findUserByJwt(jwt);
		List<Order> allOrdersOfUser = orderService.getAllOrdersOfUser(user.getId(), orderType, assetSymbol);
		System.out.println("Hello orders");
		return ResponseEntity.ok(allOrdersOfUser);
	}

}
