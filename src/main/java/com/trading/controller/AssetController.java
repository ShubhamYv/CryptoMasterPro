package com.trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.modal.Asset;
import com.trading.modal.User;
import com.trading.service.AssetService;
import com.trading.service.UserService;

@RestController
@RequestMapping("/api/asset")
public class AssetController {

	@Autowired
	private AssetService assetService;

	@Autowired
	private UserService userService;

	@GetMapping("/{assetId}")
	public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) 
			throws Exception {
		Asset asset = assetService.getAssetById(assetId);
		return ResponseEntity.ok(asset);
	}

	@GetMapping("/coin/{coinId}/user")
	public ResponseEntity<Asset> getAssetByUserIdAndCoinId(@PathVariable String coinId,
			@RequestHeader("Authorization") String jwt) throws Exception {

		User user = userService.findUserByJwt(jwt);
		Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);
		return ResponseEntity.ok(asset);
	}

	@GetMapping()
	public ResponseEntity<List<Asset>> getAssetsForUser(@RequestHeader("Authorization") String jwt) 
			throws Exception {
		User user = userService.findUserByJwt(jwt);
		List<Asset> assets = assetService.getUsersAssets(user.getId());
		return ResponseEntity.ok().body(assets);
	}
}
