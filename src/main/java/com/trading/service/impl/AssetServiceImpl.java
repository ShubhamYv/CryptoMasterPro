package com.trading.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.modal.Asset;
import com.trading.modal.Coin;
import com.trading.modal.User;
import com.trading.repository.AssetRepository;
import com.trading.service.AssetService;

@Service
public class AssetServiceImpl implements AssetService {

	@Autowired
	private AssetRepository assetRepository;

	@Override
	public Asset createAsset(User user, Coin coin, double quantity) {
		Asset asset = new Asset();
		asset.setUser(user);
		asset.setCoin(coin);
		asset.setQuantity(quantity);
		asset.setBuyPrice(coin.getCurrentPrice());
		return assetRepository.save(asset);
	}

	@Override
	public Asset getAssetById(Long assetId) throws Exception {
		return assetRepository.findById(assetId).orElseThrow(() -> new Exception("Asset not found."));
	}

	@Override
	public Asset getAssetByUserIdAndId(Long userId, Long assetId) {
		return assetRepository.findAssetByUserIdAndId(userId, assetId);
	}

	@Override
	public List<Asset> getUsersAssets(Long userId) {
		return assetRepository.findByUserId(userId);
	}

	@Override
	public Asset updateAsset(Long assetId, double quantity) throws Exception {
		Asset asset = getAssetById(assetId);
		asset.setQuantity(quantity + asset.getQuantity());
		return assetRepository.save(asset);
	}

	@Override
	public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
		return assetRepository.findByUserIdAndCoinId(userId, coinId);
	}

	@Override
	public void deleteAsset(Long assetId){
		assetRepository.deleteById(assetId);
	}

}
