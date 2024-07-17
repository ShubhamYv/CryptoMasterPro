package com.trading.service;

import java.util.List;

import com.trading.modal.Asset;
import com.trading.modal.Coin;
import com.trading.modal.User;

public interface AssetService {

	Asset createAsset(User user, Coin coin, double quantity);

	Asset getAssetById(Long assetId) throws Exception;

	Asset getAssetByUserIdAndId(Long userId, Long assetId);

	List<Asset> getUsersAssets(Long userId);

	Asset updateAsset(Long assetId, double quantity) throws Exception;

	Asset findAssetByUserIdAndCoinId(Long userId, String coinId);

	void deleteAsset(Long assetId);
}
