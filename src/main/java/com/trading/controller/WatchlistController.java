package com.trading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.modal.Coin;
import com.trading.modal.User;
import com.trading.modal.Watchlist;
import com.trading.service.CoinService;
import com.trading.service.UserService;
import com.trading.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

	@Autowired
	private WatchlistService watchlistService;

	@Autowired
	private UserService userService;

	@Autowired
	private CoinService coinService;

	@GetMapping("/user")
	public ResponseEntity<Watchlist> getUserWatchlist(@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwt(jwt);
		Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
		return ResponseEntity.ok(watchlist);
	}

	@PostMapping("/create")
	public ResponseEntity<Watchlist> createWatchlist(@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwt(jwt);
		Watchlist watchlist = watchlistService.createWatchlist(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(watchlist);
	}

	@GetMapping("/{watchlistId}")
	public ResponseEntity<Watchlist> getWatchlistById(@PathVariable Long watchlistId) throws Exception {
		Watchlist watchlist = watchlistService.findById(watchlistId);
		return ResponseEntity.ok(watchlist);
	}

	@PatchMapping("/add/coin/{coinId}")
	public ResponseEntity<Coin> addItemToWatchlist(@RequestHeader("Authorization") String jwt,
			@PathVariable String coinId) throws Exception {
		User user = userService.findUserByJwt(jwt);
		Coin coin = coinService.findById(coinId);
		Coin addCoin = watchlistService.addItemToWatchlist(coin, user);
		return new ResponseEntity<Coin>(addCoin, HttpStatus.OK);
	}
}
