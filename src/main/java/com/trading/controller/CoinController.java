package com.trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.modal.Coin;
import com.trading.service.CoinService;

@RestController
@RequestMapping("/coins")
public class CoinController {

	@Autowired
	private CoinService coinService;

	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping
	public ResponseEntity<List<Coin>> getCoinList(@RequestParam(required = false, name = "page") int page)
			throws Exception {
		List<Coin> coinList = coinService.getCoinList(page);
		return new ResponseEntity<List<Coin>>(coinList, HttpStatus.OK);
	}

	@GetMapping("/{coinId}/chart")
	public ResponseEntity<JsonNode> getMarketChart(@PathVariable String coinId, @RequestParam("days") int days)
			throws Exception {
		String chart = coinService.getMarketChart(coinId, days);
		JsonNode jsonNode = objectMapper.readTree(chart);
		return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
	}

	@GetMapping("/search")
	public ResponseEntity<JsonNode> getMarketChart(@RequestParam("query") String keyword) throws Exception {
		String searchCoin = coinService.searchCoin(keyword);
		JsonNode jsonNode = objectMapper.readTree(searchCoin);
		return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
	}

	@GetMapping("/top50")
	public ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws Exception {
		String top50CoinsByMarketCapRank = coinService.getTop50CoinsByMarketCapRank();
		JsonNode jsonNode = objectMapper.readTree(top50CoinsByMarketCapRank);
		return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
	}

	@GetMapping("/trading")
	public ResponseEntity<JsonNode> getTradingCoin() throws Exception {
		String tradingCoin = coinService.getTradingCoins();
		JsonNode jsonNode = objectMapper.readTree(tradingCoin);
		return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
	}

	@GetMapping("/details/{coinId}")
	public ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
		String coinDetails = coinService.getCoinDetails(coinId);
		JsonNode jsonNode = objectMapper.readTree(coinDetails);
		return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
	}
}
