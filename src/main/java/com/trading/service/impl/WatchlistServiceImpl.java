package com.trading.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.modal.Coin;
import com.trading.modal.User;
import com.trading.modal.Watchlist;
import com.trading.repository.WatchlistRepository;
import com.trading.service.WatchlistService;

@Service
public class WatchlistServiceImpl implements WatchlistService {

	@Autowired
	private WatchlistRepository watchlistRepository;
	
	
	@Override
	public Watchlist findUserWatchlist(Long userId) throws Exception {
		Watchlist watchlist = watchlistRepository.findByUserId(userId);
		if(watchlist == null) {
			throw new Exception("Watchlist not found.");
		}
		return watchlist;
	}

	@Override
	public Watchlist createWatchlist(User user) {
		Watchlist watchlist = new Watchlist();
		watchlist.setUser(user);
		return watchlistRepository.save(watchlist);
	}

	@Override
	public Watchlist findById(Long id) throws Exception {
		return watchlistRepository.findById(id)
				.orElseThrow(() -> new Exception("Watchlist not found with id::"+ id));
	}

	@Override
	public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
		Watchlist watchlist = findUserWatchlist(user.getId());
		
		if (watchlist.getCoins().contains(coin)) {
			watchlist.getCoins().remove(coin);
		}else {
			watchlist.getCoins().add(coin);
		}
		
		watchlistRepository.save(watchlist);
		return coin;
	}

}
