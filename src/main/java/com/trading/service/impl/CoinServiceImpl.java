package com.trading.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.modal.Coin;
import com.trading.pojo.response.CoinRepository;
import com.trading.service.CoinService;

@Service
public class CoinServiceImpl implements CoinService {

	@Autowired
	private CoinRepository coinRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${coinGecko_URL}")
	String coinGecko_url;

	@Override
	public List<Coin> getCoinList(int page) throws Exception {
		String url = coinGecko_url+ "coins/markets?vs_currency=usd&per_page=10&page=" + page;
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, 
					httpEntity, String.class);
			List<Coin> coinList = objectMapper.readValue(
					response.getBody(), 
					new TypeReference<List<Coin>>() {});
			return coinList;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String getMarketChart(String coinId, int days) throws Exception {
	    String url = coinGecko_url + "coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;
	    RestTemplate restTemplate = new RestTemplate();
	    try {
	        HttpHeaders headers = new HttpHeaders();
	        HttpEntity<String> httpEntity = new HttpEntity<>("parameters", headers);
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
	        return response.getBody();
	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        throw new Exception("Error fetching market chart for coinId: " + coinId + ", " + e.getMessage(), e);
	    }
	}


	@Override
	public String getCoinDetails(String coinId) throws Exception {
		String url = coinGecko_url + "coins/" + coinId;
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			
			Coin coin = new Coin();
			coin.setId(jsonNode.get("id").asText());
			coin.setName(jsonNode.get("name").asText());
			coin.setSymbol(jsonNode.get("symbol").asText());
			coin.setImage(jsonNode.get("image").get("large").asText());
			
			JsonNode marketData = jsonNode.get("market_data");
			
			coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
			coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
			coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
			coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
			coin.setFullyDilutedValuation(marketData.get("fully_diluted_valuation").get("usd").asLong());
			coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
			coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());			
			coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
			coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
			coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());		
			coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
			coin.setCirculatingSupply(marketData.get("circulating_supply").asDouble());
			coin.setTotalSupply(marketData.get("total_supply").asDouble());
			coin.setAth(marketData.get("ath").get("usd").asDouble());
			
			coin.setAthChangePercentage(marketData.get("ath_change_percentage").get("usd").asDouble());
			coin.setAthDate(marketData.get("ath_date").get("usd").asText());
			coin.setAtl(marketData.get("atl").get("usd").asDouble());
			coin.setAtlChangePercentage(marketData.get("atl_change_percentage").get("usd").asDouble());
			coin.setMaxSupply(marketData.get("max_supply").asDouble());
			
			coinRepository.save(coin);

			return response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public Coin findById(String coinId) throws Exception {
		return coinRepository.findById(coinId)
				.orElseThrow(() -> new Exception("Coin not found with the id:" + coinId));
	}

	@Override
	public String searchCoin(String keyword) throws Exception {
		String url = coinGecko_url+"search?query=" + keyword;
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, 
					httpEntity, String.class);
			return response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String getTop50CoinsByMarketCapRank() throws Exception {
		String url = coinGecko_url + "coins/markets?vs_currency=usd&per_page=50&page=1";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, 
					httpEntity, String.class);
			return response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String getTrendingCoins() throws Exception {
		String url = coinGecko_url +"search/trending";
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, 
					httpEntity, String.class);
			return response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new Exception(e.getMessage());
		}
	}
}
