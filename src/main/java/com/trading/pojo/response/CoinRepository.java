package com.trading.pojo.response;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trading.modal.Coin;

public interface CoinRepository extends JpaRepository<Coin, String>{

}
